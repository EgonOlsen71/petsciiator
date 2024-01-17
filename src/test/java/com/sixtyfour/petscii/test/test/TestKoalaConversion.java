package com.sixtyfour.petscii.test.test;

import com.sixtyfour.petscii.*;

public class TestKoalaConversion {

    public static void main(String[] args) {

        String[] pics = {"test.jpg", "donkey.jpg", "memotech.jpg", "alps.jpg", "trees.jpg"};
        float gamma = 1.01f;

        for (String pic:pics) {
            String targetPic = pic.replace(".jpg", "");
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_full.koa", gamma, 1, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_half.koa", gamma, 0.5f, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_mild.koa", gamma, 0.1f, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_none.koa", gamma, 0, true);
        }
    }

}
