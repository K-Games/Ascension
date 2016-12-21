package blockfighter.client.net;

import blockfighter.client.AscensionClient;
import blockfighter.client.LogicModule;
import blockfighter.client.entities.emotes.Emote;
import blockfighter.client.entities.particles.Particle;
import blockfighter.client.screen.ScreenIngame;
import blockfighter.client.screen.ScreenLoading;
import blockfighter.client.screen.ScreenServerList;
import blockfighter.shared.Globals;

public class PacketHandler {

    private static GameClient gameClient;
    private static LogicModule logic;

    public static void init() {
        logic = AscensionClient.getLogicModule();
    }

    public static void setGameClient(final GameClient cl) {
        gameClient = cl;
    }

    public static void process(final byte[] data) {
        final byte dataType = data[0];
        switch (dataType) {
            case Globals.DATA_PLAYER_LOGIN:
                receiveLogin(data);
                break;
            case Globals.DATA_PLAYER_CREATE:
                receiveCreate(data);
                break;
            default:
                receiveData(data);
                break;
        }
    }

    private static void receiveCreate(final byte[] data) {
        final byte mapID = data[1],
                key = data[2],
                size = data[3];

        logic.stopCharacterCreateTimeout();
        logic.setMyPlayerKey(key);
        final ScreenLoading loading = new ScreenLoading();
        logic.setScreen(loading);
        try {
            loading.load(mapID);
            synchronized (loading) {
                try {
                    loading.wait();
                } catch (final InterruptedException e) {
                }
            }
            Globals.log(PacketHandler.class, "Finished loading.", Globals.LOG_TYPE_DATA, true);
            ScreenIngame ingameScreen = new ScreenIngame(size, loading.getLoadedMap(), gameClient);
            logic.setScreen(ingameScreen);
            PacketSender.sendGetAll(logic.getSelectedRoom(), key);
        } catch (final Exception e) {
            Globals.logError(e.toString(), e, true);
            Particle.unloadParticles();
            Emote.unloadEmotes();
            logic.disconnect();
            PacketSender.sendDisconnect(logic.getSelectedRoom(), key);
            logic.returnMenu();
        }
    }

    private static void receiveLogin(final byte[] data) {
        try {
            byte loginResponse = data[1];
            switch (loginResponse) {
                case Globals.LOGIN_SUCCESS:
                    try {
                        if (data[2] != Globals.GAME_MAJOR_VERSION || data[3] != Globals.GAME_MINOR_VERSION || data[4] != Globals.GAME_UPDATE_NUMBER) {
                            gameClient.shutdownClient(ScreenServerList.STATUS_WRONGVERSION);
                            return;
                        }
                    } catch (Exception e) {
                        gameClient.shutdownClient(ScreenServerList.STATUS_WRONGVERSION);
                        return;
                    }
                    break;
                case Globals.LOGIN_FAIL_UID_IN_ROOM:
                    gameClient.shutdownClient(ScreenServerList.STATUS_UIDINROOM);
                    return;
                case Globals.LOGIN_FAIL_FULL_ROOM:
                    gameClient.shutdownClient(ScreenServerList.STATUS_FULLROOM);
                    return;
                case Globals.LOGIN_FAIL_NO_ROOMS:
                    gameClient.shutdownClient(ScreenServerList.STATUS_NO_ROOMS);
                    return;
                default:
                    gameClient.shutdownClient((byte) -1);
                    return;
            }
            logic.setSelectedRoom(data[5]);
            PacketSender.sendPlayerCreate(logic.getSelectedRoom(), logic.getSelectedChar());
            logic.startCharacterCreateTimeout();
        } catch (Exception e) {
            gameClient.shutdownClient(ScreenServerList.STATUS_FAILEDCONNECT);
        }
    }

    private static void receiveData(final byte[] data) {
        gameClient.queueData(data);
    }

}
