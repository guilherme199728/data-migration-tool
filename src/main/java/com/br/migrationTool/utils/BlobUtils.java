package com.br.migrationTool.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.SQLException;

public class BlobUtils {

    public static ByteArrayInputStream convertHexStringToBlob(String strBlob) {
        if (strBlob == null ) {
            return null;
        }

        try {
            byte[] blobBytes = Hex.decodeHex(strBlob);
            return new ByteArrayInputStream(blobBytes);
        } catch (DecoderException e) {
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
