package cn.lovezsm.locationsystem.udpServer.server;


public abstract class UDPServer {

//    UDPConfig config;

    private boolean open = false;

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public abstract void start();
    public abstract void stop();
}
