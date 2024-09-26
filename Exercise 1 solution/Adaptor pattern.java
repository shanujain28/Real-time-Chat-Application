interface PaymentProcessor {
    void processPayment(double amount);
}

class ExistingPaymentSystem implements PaymentProcessor {
    @Override
    public void processPayment(double amount) {
        System.out.println("Processing payment of $" + amount + " through existing system");
    }
}

class ThirdPartyPaymentGateway {
    public void sendPayment(String amount) {
        System.out.println("Sending payment of " + amount + " through third-party gateway");
    }
}

class PaymentGatewayAdapter implements PaymentProcessor {
    private ThirdPartyPaymentGateway gateway;

    public PaymentGatewayAdapter(ThirdPartyPaymentGateway gateway) {
        this.gateway = gateway;
    }

    @Override
    public void processPayment(double amount) {
        String formattedAmount = String.format("%.2f", amount);
        gateway.sendPayment(formattedAmount);
    }
}

public class PaymentGatewayDemo {
    public static void main(String[] args) {
        PaymentProcessor existingSystem = new ExistingPaymentSystem();
        existingSystem.processPayment(100.00);

        ThirdPartyPaymentGateway thirdPartyGateway = new ThirdPartyPaymentGateway();
        PaymentProcessor adapter = new PaymentGatewayAdapter(thirdPartyGateway);
        adapter.processPayment(100.00);
    }
}