package me.monster.xlog_decode.tool;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;

import javax.crypto.KeyAgreement;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class ECDHUtils {

    public static void init(){
        Security.addProvider(new BouncyCastleProvider());
    }

    public static PublicKey loadPublicKey(byte[] data) throws Exception {
        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPublicKeySpec pubKey = new ECPublicKeySpec(
                params.getCurve().decodePoint(data), params);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
        return kf.generatePublic(pubKey);
    }

    /**
     * SM2算法生成密钥对
     *
     * @return 密钥对信息
     */
    public static me.monster.xlog_decode.bean.KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256k1");
            generator.initialize(ecGenParameterSpec, new SecureRandom());
            generator.initialize(256);
            final KeyPair keyPair = generator.generateKeyPair();
            final String pub = CommonUtils.bytesToHexString(keyPair.getPrivate().getEncoded());
            System.out.println("priKey：" + pub);
            final String pri = CommonUtils.bytesToHexString(keyPair.getPublic().getEncoded());
            System.out.println("pubKey：" + pri);
            return new me.monster.xlog_decode.bean.KeyPair(pub, pri);
        } catch (Exception e) {
            e.printStackTrace();
            return new me.monster.xlog_decode.bean.KeyPair("pub", "pri");
        }
    }

    public static PrivateKey loadPrivateKey(byte[] data) throws Exception {
        PrivateKey key;
        ECParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
        ECPrivateKeySpec prvkey = new ECPrivateKeySpec(new BigInteger(data), params);
        KeyFactory kf = KeyFactory.getInstance("ECDH", "BC");
        return kf.generatePrivate(prvkey);
    }

    public static byte[] GetECDHKey(byte[] pubkey, byte[] prvkey) throws Exception{
        KeyAgreement ka = KeyAgreement.getInstance("ECDH", "BC");
        PrivateKey prvk = loadPrivateKey(prvkey);
        PublicKey pubk = loadPublicKey(pubkey);
        ka.init(prvk);
        ka.doPhase(pubk, true);
        byte[] secret = ka.generateSecret();
        //System.out.println(name + bytesToHex(secret));
        return secret;
    }
}
