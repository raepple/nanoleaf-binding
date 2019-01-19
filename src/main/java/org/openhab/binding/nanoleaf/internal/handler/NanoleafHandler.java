/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.nanoleaf.internal.handler;

import static org.openhab.binding.nanoleaf.internal.NanoleafBindingConstants.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpResponseException;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.nanoleaf.internal.NanoleafException;
import org.openhab.binding.nanoleaf.internal.NanoleafUnauthorizedException;
import org.openhab.binding.nanoleaf.internal.config.NanoleafLightPanelsConfig;
import org.openhab.binding.nanoleaf.internal.model.AuthToken;
import org.openhab.binding.nanoleaf.internal.model.BooleanState;
import org.openhab.binding.nanoleaf.internal.model.Brightness;
import org.openhab.binding.nanoleaf.internal.model.ControllerInfo;
import org.openhab.binding.nanoleaf.internal.model.Ct;
import org.openhab.binding.nanoleaf.internal.model.Effects;
import org.openhab.binding.nanoleaf.internal.model.Hue;
import org.openhab.binding.nanoleaf.internal.model.IntegerState;
import org.openhab.binding.nanoleaf.internal.model.On;
import org.openhab.binding.nanoleaf.internal.model.Palette;
import org.openhab.binding.nanoleaf.internal.model.Rhythm;
import org.openhab.binding.nanoleaf.internal.model.Sat;
import org.openhab.binding.nanoleaf.internal.model.State;
import org.openhab.binding.nanoleaf.internal.model.Write;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * The {@link NanoleafHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Martin Raepple - Initial contribution
 */
@NonNullByDefault
public class NanoleafHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(NanoleafHandler.class);

    private HttpClient httpClient;

    // Pairing interval
    private final static int PAIRING_INTERVAL = 25;

    // Pairing and status update jobs
    private @Nullable ScheduledFuture<?> pairingJob;
    private @Nullable ScheduledFuture<?> statusJob;

    // JSON parser for API responses
    private final Gson gson = new Gson();

    public NanoleafHandler(Thing thing, HttpClient httpClient) {
        super(thing);
        this.httpClient = httpClient;
        startStatusUpdate();
    }

    private void sendStateCommand(String stateClass, Command command) throws NanoleafException {
        try {
            State stateObject = new State();
            if (command instanceof DecimalType) {
                IntegerState state = (IntegerState) Class.forName(stateClass).newInstance();
                state.setValue(((DecimalType) command).intValue());
                stateObject.setState(state);
            } else if (command instanceof OnOffType) {
                BooleanState state = (BooleanState) Class.forName(stateClass).newInstance();
                state.setValue(OnOffType.ON.equals(command));
                stateObject.setState(state);
            } else {
                logger.error("Unhandled command type: {}", command.getClass().getName());
                throw new NanoleafException("Unhandled command type");
            }
            Request setNewStateRequest = requestBuilder(API_SET_VALUE, HttpMethod.PUT);
            setNewStateRequest.content(new StringContentProvider(gson.toJson(stateObject)), "application/json");
            sendOpenAPIRequest(setNewStateRequest);
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            logger.error("Failed to send command");
            throw new NanoleafException(String.format("Send state command failed: %s", e.getMessage()));
        }
    }

    private void sendEffectCommand(Command command) throws NanoleafException {
        Effects effects = new Effects();
        if (command instanceof HSBType) {
            Write write = new Write();
            write.setCommand("display");
            write.setAnimType("solid");
            Palette palette = new Palette();
            palette.setBrightness(((HSBType) command).getBrightness().intValue());
            palette.setHue(((HSBType) command).getHue().intValue());
            palette.setSaturation(((HSBType) command).getSaturation().intValue());
            List<Palette> palettes = new ArrayList<Palette>();
            palettes.add(palette);
            write.setPalette(palettes);
            write.setColorType("HSB");
            effects.setWrite(write);
        } else if (command instanceof StringType) {
            effects.setSelect(command.toString());
        } else if (command instanceof OnOffType) {
            sendStateCommand(On.class.getName(), command);
        } else {
            logger.error("Unhandled command type: {}", command.getClass().getName());
            throw new NanoleafException("Unhandled command type");
        }
        Request setNewEffectRequest = requestBuilder(API_SELECT_EFFECT, HttpMethod.PUT);
        setNewEffectRequest.content(new StringContentProvider(gson.toJson(effects)), "application/json");
        sendOpenAPIRequest(setNewEffectRequest);
    }

    private void sendRhythmCommand(Command command) throws NanoleafException {
        Rhythm rhythm = new Rhythm();
        if (command instanceof DecimalType) {
            rhythm.setRhythmMode(((DecimalType) command).intValue());
        } else {
            logger.error("Unhandled command type: {}", command.getClass().getName());
            throw new NanoleafException("Unhandled command type");
        }
        Request setNewRhythmRequest = requestBuilder(API_RHYTHM_MODE, HttpMethod.PUT);
        setNewRhythmRequest.content(new StringContentProvider(gson.toJson(rhythm)), "application/json");
        sendOpenAPIRequest(setNewRhythmRequest);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        logger.debug("Received channel: {}, command: {}", channelUID, command);
        if (command instanceof RefreshType) {
            updateFromControllerInfo();
        } else {
            switch (channelUID.getId()) {
                case CHANNEL_POWER:
                    sendStateCommand(On.class.getName(), command);
                    break;
                case CHANNEL_BRIGHTNESS:
                    if (command instanceof OnOffType) {
                        sendStateCommand(On.class.getName(), command);
                    } else {
                        sendStateCommand(Brightness.class.getName(), command);
                    }
                    break;
                case CHANNEL_HUE:
                    sendStateCommand(Hue.class.getName(), command);
                    break;
                case CHANNEL_COLOR:
                    sendEffectCommand(command);
                    break;
                case CHANNEL_COLOR_TEMPERATURE:
                    sendStateCommand(Ct.class.getName(), command);
                    break;
                case CHANNEL_EFFECT:
                    sendEffectCommand(command);
                    break;
                case CHANNEL_SATURATION:
                    sendStateCommand(Sat.class.getName(), command);
                    break;
                case CHANNEL_RHYTHM_MODE:
                    sendRhythmCommand(command);
                    break;
                default:
                    logger.error("Channel with id {} not handled", channelUID.getId());
                    break;
            }
        }
    }

    @Override
    public void initialize() {
        logger.debug("Start initializing light panels");
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_PENDING);

        NanoleafLightPanelsConfig config = getConfigAs(NanoleafLightPanelsConfig.class);

        if (StringUtils.isEmpty(config.ipAddress) || StringUtils.isEmpty(String.valueOf(config.port))) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.HANDLER_INITIALIZING_ERROR,
                    "IP address and/or port are not configured for the light panels.");
            // do nothing
        } else if (StringUtils.isEmpty(config.authToken)) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR, "No authorization token found.");
            startPairingJob();
        } else {
            updateStatus(ThingStatus.ONLINE);
            startStatusUpdate();
        }
    }

    @Override
    public void thingUpdated(Thing thing) {
        logger.debug("Nanoleaf {} updated", thing.getUID().getId());
        super.thingUpdated(thing);
    }

    @Override
    public void handleRemoval() {
        super.handleRemoval();
        stopPairingJob();
        stopStatusUpdate();
    }

    @Override
    public void dispose() {
        super.dispose();
        stopPairingJob();
        stopStatusUpdate();
    }

    private synchronized void startPairingJob() {
        if ((pairingJob == null || pairingJob.isCancelled())) {
            logger.debug("Start pairing job, interval={} sec", PAIRING_INTERVAL);
            pairingJob = scheduler.scheduleWithFixedDelay(pairingRunnable, 0, PAIRING_INTERVAL, TimeUnit.SECONDS);
        }
    }

    private synchronized void stopPairingJob() {
        if (pairingJob != null && !pairingJob.isCancelled()) {
            logger.debug("Stop pairing job");
            pairingJob.cancel(false);
        }
    }

    private synchronized void startStatusUpdate() {
        NanoleafLightPanelsConfig panelsConfig = getConfigAs(NanoleafLightPanelsConfig.class);
        if (StringUtils.isNotEmpty(panelsConfig.authToken)) {
            if ((statusJob == null || statusJob.isCancelled())) {
                logger.debug("Start status job, interval={} sec", panelsConfig.refreshInterval);
                statusJob = scheduler.scheduleWithFixedDelay(statusRunnable, 0, panelsConfig.refreshInterval,
                        TimeUnit.SECONDS);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_PENDING,
                    "No authorization token found. To start pairing, press the on-off button of the controller for 5-7 seconds until the LED starts flashing in a pattern.");
            startPairingJob();
        }
    }

    private synchronized void stopStatusUpdate() {
        if (statusJob != null && !statusJob.isCancelled()) {
            logger.debug("Stop status job");
            statusJob.cancel(false);
        }
    }

    private final Runnable statusRunnable = new Runnable() {
        @Override
        public void run() {
            updateFromControllerInfo();
        }
    };

    private final Runnable pairingRunnable = new Runnable() {
        @Override
        public void run() {
            URI authTokenURI;
            try {
                NanoleafLightPanelsConfig panelsConfig = getConfigAs(NanoleafLightPanelsConfig.class);
                if (StringUtils.isNotEmpty(panelsConfig.authToken)) {
                    pairingJob.cancel(false);
                    return;
                }

                if (httpClient.isStarted()) {
                    httpClient.stop();
                }
                httpClient.start();
                ContentResponse authTokenResponse = requestBuilder(API_ADD_USER, HttpMethod.POST).send();
                logger.trace("Auth token response {}", authTokenResponse.getContentAsString());
                logger.debug("Auth token response code: {}", authTokenResponse.getStatus());

                if (authTokenResponse.getStatus() != HttpStatus.OK_200) {
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                            "Pairing failed. Press the on-off button for 5-7 seconds until the LED starts flashing in a pattern.");
                    return;
                } else {
                    // get auth token from response
                    AuthToken authToken = gson.fromJson(authTokenResponse.getContentAsString(), AuthToken.class);

                    if (StringUtils.isNotEmpty(authToken.getAuthToken())) {
                        logger.debug("Pairing succeeded.");

                        // Update and save the auth token in the thing configuration
                        Configuration config = editConfiguration();
                        config.put(NanoleafLightPanelsConfig.AUTH_TOKEN, authToken.getAuthToken());
                        updateConfiguration(config);
                        updateStatus(ThingStatus.ONLINE);
                    } else {
                        logger.debug("Auth token response: {}", authTokenResponse.getContentAsString());
                        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                                "Pairing failed. Retry by holding the on-off button down for 5-7 seconds until the LED starts flashing in a pattern.");
                        throw new NanoleafException(authTokenResponse.getContentAsString());
                    }
                }
            } catch (JsonSyntaxException e) {
                logger.debug("Received invalid data", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Pairing failed: Received invalid data");
            } catch (NanoleafException e) {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                        "Pairing failed. No authorization token in response.");
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                logger.debug("Cannot send authorization request to light panels", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Pairing failed. Cannot send authorization request.");
            } catch (Exception e) {
                logger.debug("Cannot start http client", e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                        "Pairing failed. Cannot start HTTP client");
            }
        }
    };

    private void updateFromControllerInfo() {
        logger.debug("Update channels for light panels {}", thing.getUID());
        NanoleafLightPanelsConfig panelsConfig = getConfigAs(NanoleafLightPanelsConfig.class);
        try {
            ContentResponse controllerInfoJSON = sendOpenAPIRequest(
                    requestBuilder(API_GET_CONTROLLER_INFO, HttpMethod.GET));
            ControllerInfo controllerInfo = gson.fromJson(controllerInfoJSON.getContentAsString(),
                    ControllerInfo.class);

            // update channels
            updateState(CHANNEL_POWER, controllerInfo.getState().getOn().getValue() ? OnOffType.ON : OnOffType.OFF);
            updateState(CHANNEL_BRIGHTNESS,
                    new PercentType(controllerInfo.getState().getBrightness().getValue().intValue()));
            updateState(CHANNEL_COLOR_TEMPERATURE,
                    new DecimalType(controllerInfo.getState().getCt().getValue().intValue()));
            updateState(CHANNEL_HUE, new DecimalType(controllerInfo.getState().getHue().getValue().intValue()));
            updateState(CHANNEL_SATURATION, new PercentType(controllerInfo.getState().getSat().getValue().intValue()));
            updateState(CHANNEL_EFFECT, new StringType(controllerInfo.getEffects().getSelect()));
            updateState(CHANNEL_COLOR_MODE, new StringType(controllerInfo.getState().getColorMode()));
            updateState(CHANNEL_RHYTHM_ACTIVE,
                    controllerInfo.getRhythm().getRhythmActive().booleanValue() ? OnOffType.ON : OnOffType.OFF);
            updateState(CHANNEL_RHYTHM_MODE, new DecimalType(controllerInfo.getRhythm().getRhythmMode().intValue()));
            updateState(CHANNEL_RHYTHM_STATE,
                    controllerInfo.getRhythm().getRhythmConnected().booleanValue() ? OnOffType.ON : OnOffType.OFF);

            // update properties
            Map<String, String> properties = editProperties();
            properties.put(Thing.PROPERTY_SERIAL_NUMBER, controllerInfo.getSerialNo());
            properties.put(Thing.PROPERTY_FIRMWARE_VERSION, controllerInfo.getFirmwareVersion());
            properties.put(Thing.PROPERTY_VENDOR, controllerInfo.getManufacturer());
            properties.put(Thing.PROPERTY_MODEL_ID, controllerInfo.getModel());
            updateProperties(properties);

        } catch (NanoleafUnauthorizedException nae) {
            logger.error("Update channels failed: {}", nae.getMessage());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Invalid token. Replace with valid token or start pairing again.");
            if (StringUtils.isEmpty(panelsConfig.authToken)) {
                startPairingJob();
            }
        } catch (NanoleafException ne) {
            logger.error("Update channels failed: {}", ne.getMessage());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
                    "Communication failed. Please check configuration");
        }
    }

    private ContentResponse sendOpenAPIRequest(Request request) throws NanoleafException {
        try {
            ContentResponse openAPIResponse = request.send();
            logger.trace("API response from Nanoleaf controller: {}", openAPIResponse.getContentAsString());
            logger.debug("API response code: {}", openAPIResponse.getStatus());
            int responseStatus = openAPIResponse.getStatus();
            if (responseStatus == HttpStatus.OK_200 || responseStatus == HttpStatus.NO_CONTENT_204) {
                return openAPIResponse;
            } else {
                if (openAPIResponse.getStatus() == HttpStatus.UNAUTHORIZED_401) {
                    throw new NanoleafUnauthorizedException("OpenAPI request unauthorized");
                } else {
                    throw new NanoleafException(String.format("OpenAPI request failed. HTTP response code %s",
                            openAPIResponse.getStatus()));
                }
            }
        } catch (ExecutionException | TimeoutException | InterruptedException clientException) {
            if (clientException.getCause() instanceof HttpResponseException) {
                if (((HttpResponseException) clientException.getCause()).getResponse()
                        .getStatus() == HttpStatus.UNAUTHORIZED_401) {
                    logger.error("OpenAPI request unauthorized. Invalid authorization token.");
                    throw new NanoleafUnauthorizedException("Invalid authorization token");
                }
            }
            throw new NanoleafException(
                    String.format("Failed to send OpenAPI request: %s", clientException.getMessage()));
        }
    }

    private Request requestBuilder(String apiOperation, HttpMethod method) {
        String path;
        NanoleafLightPanelsConfig panelsConfig = getConfigAs(NanoleafLightPanelsConfig.class);
        if (apiOperation.equals(API_ADD_USER)) {
            path = String.format("%s%s", API_V1_BASE_URL, apiOperation);
        } else {
            path = String.format("%s/%s%s", API_V1_BASE_URL, panelsConfig.authToken, apiOperation);
        }
        URI requestURI;
        try {
            requestURI = new URI(HttpScheme.HTTP.asString(), null, panelsConfig.ipAddress, panelsConfig.port, path,
                    null, null);
        } catch (URISyntaxException use) {
            logger.error(String.format("URI could not be parsed with path %s", path));
            throw new NanoleafException("Wrong URI format for API request");
        }
        return httpClient.newRequest(requestURI).method(method);
    }
}
