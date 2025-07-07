package com.sixtyfour.petscii.test.test;

import java.util.Arrays;

import com.sixtyfour.petscii.*;

public class TestHiEddiConversion {

    public static void main(String[] args) {

        String[] pics = {"small.png", "flowers.jpg", "test.jpg", "donkey.jpg", "memotech.jpg", "alps.jpg", "trees.jpg", "grumpy cat.jpg", "Mouse.png", "lines.png", "hieddi_demo.png"};
        float gamma = 1f;
        ColorMap colors = new Vic2Colors();

        Arrays.asList(pics).parallelStream().forEach(p -> {
            String targetPic = p.replace(".jpg", "").replace(".png", "");
            HiEddiConverter.convert("examples/koala/"+p, "results/"+targetPic+"_full.hed", colors, gamma, 1, true);
            HiEddiConverter.convert("examples/koala/"+p, "results/"+targetPic+"_half.hed", colors, gamma, 0.5f, true);
            HiEddiConverter.convert("examples/koala/"+p, "results/"+targetPic+"_mild.hed", colors, gamma, 0.1f, true);
            HiEddiConverter.convert("examples/koala/"+p, "results/"+targetPic+"_none.hed", colors, gamma, 0, true);
        });
    /*    
        pics = new String[]{"dragon.jpg", "coast.png", "painting.jpg"};

        for (String pic:pics) {
            String targetPic = pic.replace(".jpg", "").replace(".png", "");
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_full.koa", colors, gamma, 1, true, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_half.koa", colors, gamma, 0.5f, true, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_mild.koa", colors, gamma, 0.1f, true, true);
            KoalaConverter.convert("examples/koala/"+pic, "results/"+targetPic+"_none.koa", colors, gamma, 0, true, true);
        }
        //KoalaConverter.convert("examples/dog.png", "results/dog_cropped.koa", colors, gamma, 0.75f, true, true, true);
        KoalaConverter.convert("examples/koala/d42test.png", "results/d42test.koa", colors, gamma, 0.75f, true, false, true, true);
        */
    }

}
