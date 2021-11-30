package me.monster.xlog_decode.bean;

public class KeyPair {
    private String pubKey;
    private String priKey;

    public KeyPair(String pubKey, String priKey) {
        this.pubKey = pubKey;
        this.priKey = priKey;
    }

    public String getPubKey() {
        return pubKey == null ? "" : pubKey;
    }

    public void setPubKey(String pubKey) {
        this.pubKey = pubKey;
    }

    public String getPriKey() {
        return priKey == null ? "" : priKey;
    }

    public void setPriKey(String priKey) {
        this.priKey = priKey;
    }
}
