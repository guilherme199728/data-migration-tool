package com.br.migrationTool.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;

public class BlobUtils {

    public static Blob convertHexStringToBlob(String strBlob) {
        if (strBlob == null ) {
            return null;
        }

        try {
            byte[] blobBytes = Hex.decodeHex(strBlob);
            return new SerialBlob(blobBytes);
        } catch (DecoderException | SQLException e) {
            // TODO: tratar o erro aqui
        }
        return null;
    }

    public static String convertBlobToHexString(Blob blob) throws SQLException {

        if (blob == null) {
            return null;
        }
        byte[] blobBytes = blob.getBytes(1, (int) blob.length());

        return Hex.encodeHexString(blobBytes);
    }
}
