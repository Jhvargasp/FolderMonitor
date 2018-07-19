package com.grupointent.metadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

public class PDFMetadata {

	public static Map<String, Object> getMetadata(String fileName) throws IOException {
		File file = new File(fileName);
	
		final PDDocument pdDoc = PDDocument.load(file);
		try {
			PDDocumentInformation docInfo = pdDoc.getDocumentInformation();
			Set<String> keys = docInfo.getMetadataKeys();

			Map<String, Object> map = new HashMap();

			for (String key : keys) {
				map.put(key, docInfo.getPropertyStringValue(key));
			}

			pdDoc.close();
			System.out.println(map);
			getNioPiops(fileName);
			return (map);

		} catch (IOException e) {
			return null;
		}
	}

	public static Map getNioPiops(String src) throws IOException {
		Path path = Paths.get(src);
        FileOwnerAttributeView ownerAttributeView = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
        UserPrincipal owner = ownerAttributeView.getOwner();
        System.out.println("owner: " + owner.getName());
        return null;
	}

	public static void main(String[] args) {
		try {
			getMetadata("c:/tmp/2015053471.pdf");
			getMetadata("c:/tmp/2016053551.pdf");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
