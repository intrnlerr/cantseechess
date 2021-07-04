package cantseechess.chess;

import org.junit.Test;

import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

public class RatingTest {
    @Test
    public void testCalculateRatings() {
        var games = new ArrayList<Rating.GameEntry>();
        Rating player = new Rating(1500, 200);
        games.add(new Rating.GameEntry(new Rating(1400, 30), 1.0));
        games.add(new Rating.GameEntry(new Rating(1550, 100), 0.0));
        games.add(new Rating.GameEntry(new Rating(1700, 300), 0.0));
        player.calculateRating(games);
        assertEquals(1464, (int) player.getRating());
        assertEquals(151, (int) player.getDeviation());
        assertEquals(0.05999, ((int) (player.getVolatility() * 100000)) / 100000.0);
    }
}
