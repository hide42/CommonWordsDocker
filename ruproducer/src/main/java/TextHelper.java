import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class TextHelper {
    public static String getText(String S){
        String text="";
        try {
            Document doc = Jsoup.connect("http://www.megalyrics.ru/search?utf8=✓&search="+S.replace(" ","+")).timeout(0).get();
            String mainUrl = doc.select(".songs-table > table > tbody > tr > .st-title > a").first().attr("href");
            Document docA = Jsoup.connect("http://www.megalyrics.ru/"+mainUrl).get();
            System.out.println(S+" отправил "+mainUrl);
            text = docA.select(".text_inner").first().text().replaceAll("\\{.*\\}", "").toLowerCase().replaceAll("\\pP", "").replaceAll("\\s+"," ");//откуда запятые то?
        } catch (IOException | NullPointerException e) {
            System.err.println("Не найдена песня "+S);
        }
        return text;
    }

}
