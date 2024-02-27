package us.dit.model;

import java.util.Arrays;
import java.util.List;

public class Festivos {
    private List<String> festivos;

    public List<String> getFestivos() {
        return festivos;
    }

    public void setFestivos(List<String> festivos) {
        this.festivos = festivos;
    }

    @Override
    public String toString() {
        return "FestivosRequest{" +
                "festivos=" + this.getFestivos() +
                '}';
    }
}
