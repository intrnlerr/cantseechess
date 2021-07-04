package cantseechess.chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class Rating {
    private static final double DEFAULT_RATING = 1500;
    private static final double DEFAULT_DEVIATION = 350;
    private static final double DEFAULT_VOLATILITY = 0.06;
    private double rating;
    private double deviation;
    private double volatility;

    public Rating() {
        this.rating = DEFAULT_RATING;
        this.deviation = DEFAULT_DEVIATION;
        this.volatility = DEFAULT_VOLATILITY;
    }

    public Rating(double rating) {
        this.rating = rating;
        this.deviation = DEFAULT_DEVIATION;
        this.volatility = DEFAULT_VOLATILITY;
    }

    public Rating(double rating, double deviation) {
        this.rating = rating;
        this.deviation = deviation;
        this.volatility = DEFAULT_VOLATILITY;
    }

    public double getRating() {
        return rating;
    }

    public double getDeviation() {
        return deviation;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setDeviation(double deviation) {
        this.deviation = deviation;
    }

    //http://www.glicko.net/glicko/glicko2.pdf
    private static final double VOLATILITY_CHANGE = 0.5;
    private static final double GLICKO_NUMBER = 173.7178;
    private static final double CONVERGENCE_NUMBER = 0.000001;

    //takes a list of ratings and score (0 for loss, 0.5 for draw, 1 for win)
    //it work :-D
    public void calculateRating(List<GameEntry> others) {
        double mu = getMu();
        double phi = getPhi();
        double v = v(mu, others);
        double delta = delta(v, mu, others);
        double a = a();
        System.out.println("Delta: " + delta + " v: " + v + " a: " + a);
        double A = a;
        double B;
        if (Math.pow(delta, 2) > Math.pow(phi, 2) + v) {
            B = Math.log(Math.pow(delta, 2) - Math.pow(phi, 2) - v);
        } else {
            int k = 1;
            B = a - (k * VOLATILITY_CHANGE);
            while (f(a - k * VOLATILITY_CHANGE, v, mu, a, delta, phi) < 0) {
                k++;
                B = a - (k * VOLATILITY_CHANGE);
            }
        }
        double fA = f(A, v, mu, a, delta, phi);
        double fB = f(B, v, mu, a, delta, phi);
        double C;
        double fC;
        while (Math.abs(B - A) > CONVERGENCE_NUMBER) {
            C = A + ((A - B) * fA) / (fB - fA);
            fC = f(C, v, mu, a, delta, phi);
            if (fC * fB < 0) {
                A = B;
                fA = fB;
            } else {
                fA /= 2;
            }
            B = C;
            fB = fC;
        }
        volatility = Math.exp(A / 2.0);
        double newPhi = Math.sqrt(Math.pow(phi, 2) + Math.pow(volatility, 2));
        //TODO if the player does not compete during the rating period then stop here
        deviation = 1.0 / Math.sqrt(1.0 / Math.pow(newPhi, 2) + 1.0 / v);
        rating = mu + delta(Math.pow(deviation, 2), mu, others);

        rating = GLICKO_NUMBER * rating + 1500;
        deviation = GLICKO_NUMBER * deviation;
    }

    double g(double phi) {
        return 1 / (Math.sqrt(1 + 3 * Math.pow(phi, 2) / Math.pow(Math.PI, 2)));
    }

    double E(double mu, double muj, double phij) {
        return 1 / (1 + Math.exp(-1 * g(phij) * (mu - muj)));
    }

    double v(double mu, List<GameEntry> entries) {
        double toReturn = 0;
        for (GameEntry r : entries) {
            toReturn += Math.pow(g(r.rating.getPhi()), 2) * E(mu, r.rating.getMu(), r.rating.getPhi()) * (1 - E(mu, r.rating.getMu(), r.rating.getPhi()));
        }
        return Math.pow(toReturn, -1);
    }

    double delta(double v, double mu, List<GameEntry> others) {
        double toReturn = 0;
        for (GameEntry entry : others) {
            toReturn += g(entry.rating.getPhi()) * (entry.result - E(mu, entry.rating.getMu(), entry.rating.getPhi()));
        }
        return v * toReturn;
    }

    double a() {
        return Math.log(Math.pow(volatility, 2));
    }

    double f(double x, double v, double mu, double a, double delta, double phi) {
        return (Math.pow(Math.E, x) * (Math.pow(delta, 2) - Math.pow(phi, 2) - v - Math.pow(Math.E, x))) / (2 * Math.pow(Math.pow(phi, 2) + v + Math.pow(Math.E, x), 2)) - ((x - a) / Math.pow(VOLATILITY_CHANGE, 2));
    }

    public double getMu() {
        return (rating - DEFAULT_RATING) / GLICKO_NUMBER;
    }

    public double getPhi() {
        return deviation / GLICKO_NUMBER;
    }

    static class GameEntry {
        public final Rating rating;
        public final double result;

        GameEntry(Rating rating, double result) {
            this.rating = rating;
            this.result = result;
        }
    }
}
