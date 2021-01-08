package uk.co.lightapps.app.forex.charts.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Asif Akhtar
 * 05/01/2021 21:43
 */
@Data
public class ChartContent {
    private List<String> labels = new ArrayList<>();
    private List<String> values = new ArrayList<>();

    public void add(String label, String value) {
        labels.add(label);
        values.add(value);
    }
}
