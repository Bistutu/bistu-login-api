package com.thinkstu.helper;

import net.sourceforge.tess4j.*;

import java.io.*;
import java.util.*;

public class TesseractOCRHelper {
    public static final int MAX_TRY_TIMES = 20;

    public static String doOcr(String path) throws TesseractException, FileNotFoundException {
        ITesseract instance = new Tesseract();
        // TODO 这行报错，找不到 tesseract
        String result = instance.doOCR(new File(path));
/*        Scanner sc     = new Scanner(new FileReader("/Users/thinkstu/Desktop/test/1.txt"));
        String  data   = sc.nextLine();
        String  result = data;*/
        result = result.replaceAll("\\s+", "");
        return result;
    }
}
