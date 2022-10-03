package Packages.Vanilla;

import GameContent.PackageCore;

import java.awt.*;

public class Vanilla implements PackageCore {

    @Override
    public void frame(Graphics g) {
        g.drawString("hello world!", 200, 200);
    }

    @Override
    public void tick() {

    }

    @Override
    public void initialize() {
        System.out.println("hello from the vanilla package");
    }

}
