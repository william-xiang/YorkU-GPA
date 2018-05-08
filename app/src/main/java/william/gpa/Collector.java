package william.gpa;

import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Collector {
    Elements elements;
    static List<List<String>> detail;
    List<String> subjects;

    public Collector(Elements elements) {
        this.elements = elements;
        this.collect();
    }

    public void collect() {
        detail = new ArrayList<>();
        subjects = new ArrayList<>();
        subjects.add("");

        for (int i = 0; i < elements.size(); i += 4) {
            String mark = elements.get(i + 3).text();

            if (!mark.equals("") && !mark.equals("R") && !mark.equals("W") && !mark.equals("P")) {
                List<String> detailElement = new ArrayList<>();

                for (int j = i; j < i + 4; j++) {
                    if (j == i + 1) {
                        String[] split = elements.get(j).text().split(" ");
                        detailElement.add(split[1] + " " + split[2]);
                        detailElement.add(split[3]);

                        if (!subjects.contains(split[1])) {
                            subjects.add(split[1]);
                        }
                    } else if (j != i + 2) {
                        detailElement.add(elements.get(j).text());
                    }
                }

                detail.add(detailElement);
            }
        }
    }

    public static List<List<String>> getDetail() {
        return detail;
    }

    public List<String> getSubjects() {
        return subjects;
    }
}
