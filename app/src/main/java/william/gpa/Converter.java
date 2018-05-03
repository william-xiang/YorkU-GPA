package william.gpa;

import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import org.jsoup.select.Elements;
import java.util.HashMap;
import java.util.Map;

public class Converter {
    Elements elements;

    public Converter(Elements elements) {
        this.elements = elements;
    }

    public CharSequence getResult() {
        String tempResult = "";
        Map<String, Integer> conversionChart9 = new HashMap<>();
        Map<String, Double> conversionChart4 = new HashMap<>();

        conversionChart9.put("A+", 9);
        conversionChart9.put("A", 8);
        conversionChart9.put("B+", 7);
        conversionChart9.put("B", 6);
        conversionChart9.put("C+", 5);
        conversionChart9.put("C", 4);
        conversionChart9.put("D+", 3);
        conversionChart9.put("D", 2);
        conversionChart9.put("E", 1);
        conversionChart9.put("F", 0);
        conversionChart9.put("NC", 0);

        conversionChart4.put("A+", 4.0);
        conversionChart4.put("A", 3.8);
        conversionChart4.put("B+", 3.3);
        conversionChart4.put("B", 3.0);
        conversionChart4.put("C+", 2.3);
        conversionChart4.put("C", 2.0);
        conversionChart4.put("D+", 1.3);
        conversionChart4.put("D", 1.0);
        conversionChart4.put("E", 0.0);
        conversionChart4.put("F", 0.0);
        conversionChart4.put("NC", 0.0);

        double credit = 0;
        double credits = 0;
        double pointes9 = 0;
        double pointes4 = 0;

        for (int i = 0; i < elements.size(); i += 4) {
            if (!elements.get(i + 3).text().equals("")) {
                for (int j = i; j < i + 4; j++) {
                    if (j == i + 1) {
                        String[] split = elements.get(j).text().split(" ");
                        tempResult += split[1] + " " + split[2] + " | ";
                        credit = Double.parseDouble(split[3]);

                        if (!split[2].equals("1001")) {
                            credits += credit;
                        }
                    }
                    else if (j != i + 3) {
                        tempResult += elements.get(j).text() + " | ";
                    }
                    else {
                        tempResult += elements.get(j).text();
                    }

                    if (j == i + 3) {
                        String mark = elements.get(j).text();

                        if (!mark.equals("R") && !mark.equals("W") && !mark.equals("P")) {
                            pointes9 += conversionChart9.get(elements.get(j).text()) * credit;
                            pointes4 += conversionChart4.get(elements.get(j).text()) * credit;
                        }
                    }
                }
                tempResult += "\n";
            }
        }

        SpannableString title1 = new SpannableString("Your GPA is:\n");
        SpannableString result = new SpannableString(pointes9 / credits + "(9.0 Scale)\n" + pointes4 / credits + "(4.0 Scale)\n");
        SpannableString clarify = new SpannableString("-- Conversion based on OLSAS Conversion Table\n\n\n");
        SpannableString title2 = new SpannableString("Course Detail:\n");
        SpannableString detail = new SpannableString(tempResult);

        title1.setSpan(new RelativeSizeSpan(2f), 0, title1.length(), 0); // set size
        //title1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title1.length(), 0);// set color
        result.setSpan(new RelativeSizeSpan(1f), 0, result.length(), 0); // set size
        //result.setSpan(new ForegroundColorSpan(Color.BLACK), 0, result.length(), 0);// set color
        clarify.setSpan(new RelativeSizeSpan(0.8f), 0, clarify.length(), 0); // set size
        title2.setSpan(new RelativeSizeSpan(2f), 0, title2.length(), 0); // set size
        //title2.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title2.length(), 0);// set color
        detail.setSpan(new RelativeSizeSpan(1.5f), 0, detail.length(), 0); // set size
        //detail.setSpan(new ForegroundColorSpan(Color.BLACK), 0, detail.length(), 0);// set color
        return TextUtils.concat(title1, result, clarify, title2, new SpannableString(tempResult));
    }
}
