import java.util.ArrayList;
import java.util.List;

interface StockObserver {
    void update(String stockSymbol, double price);
}

class StockMarket {
    private List<StockObserver> observers = new ArrayList<>();
    private String stockSymbol;
    private double price;

    public void attach(StockObserver observer) {
        observers.add(observer);
    }

    public void detach(StockObserver observer) {
        observers.remove(observer);
    }

    public void setStockPrice(String stockSymbol, double price) {
        this.stockSymbol = stockSymbol;
        this.price = price;
        notifyObservers();
    }

    private void notifyObservers() {
        for (StockObserver observer : observers) {
            observer.update(stockSymbol, price);
        }
    }
}

class Investor implements StockObserver {
    private String name;

    public Investor(String name) {
        this.name = name;
    }

    @Override
    public void update(String stockSymbol, double price) {
        System.out.println(name + " received update: " + stockSymbol + " is now $" + price);
    }
}

public class StockMarketDemo {
    public static void main(String[] args) {
        StockMarket stockMarket = new StockMarket();
        Investor investor1 = new Investor("Alice");
        Investor investor2 = new Investor("Bob");

        stockMarket.attach(investor1);
        stockMarket.attach(investor2);

        stockMarket.setStockPrice("AAPL", 150.50);
        stockMarket.setStockPrice("GOOGL", 2750.75);

        stockMarket.detach(investor2);

        stockMarket.setStockPrice("AAPL", 151.00);
    }
}