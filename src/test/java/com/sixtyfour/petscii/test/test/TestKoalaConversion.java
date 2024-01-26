package com.sixtyfour.petscii.test.test;

import com.sixtyfour.petscii.*;

public class TestKoalaConversion {

    public static void main(String[] args) {

        String[] pics = {"flowers.jpg", "test.jpg", "donkey.jpg", "memotech.jpg", "alps.jpg", "trees.jpg", "grumpy cat.jpg", "Mouse.png"};
        float gamma = 1.01f;
        ColorMap colors = new Vic2Colors();

        for (String pic:pics) {
            String targetPic = pic.replace(".jpg", "").replace(".png", "");
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_full.koa", colors, gamma, 1, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_half.koa", colors, gamma, 0.5f, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_mild.koa", colors, gamma, 0.1f, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_none.koa", colors, gamma, 0, true);
        }
        
        pics = new String[]{"dragon.jpg", "coast.png"};

        for (String pic:pics) {
            String targetPic = pic.replace(".jpg", "").replace(".png", "");
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_full.koa", colors, gamma, 1, true, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_half.koa", colors, gamma, 0.5f, true, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_mild.koa", colors, gamma, 0.1f, true, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_none.koa", colors, gamma, 0, true, true);
        }
    }

}
