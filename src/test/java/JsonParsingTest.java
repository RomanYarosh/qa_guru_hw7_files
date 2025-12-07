import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonParsingTest {

    public static class Order {
        public int orderId;
        public String customerName;
        public boolean isDelivered;
        public List<OrderItem> items;
    }

    public static class OrderItem {
        public String name;
        public double price;
        public int quantity;
    }

    private final ClassLoader cl = JsonParsingTest.class.getClassLoader();

    @Test
    @DisplayName("Парсинг JSON файла с помощью Jackson")
    void jsonParsingTest() throws Exception {

        try (InputStream stream = cl.getResourceAsStream("order.json")) {

            ObjectMapper objectMapper = new ObjectMapper();

            Order order = objectMapper.readValue(stream, Order.class);

            assertThat(order.orderId).isEqualTo(7741);
            assertThat(order.customerName).isEqualTo("Dmitry Testov");
            assertThat(order.isDelivered).isFalse();

            assertThat(order.items).hasSize(2); // Ожидаем 2 товара

            assertThat(order.items.get(0).name).isEqualTo("MacBook Pro 16");
            assertThat(order.items.get(0).price).isEqualTo(2500);

            assertThat(order.items.get(1).name).isEqualTo("Magic Mouse");
            assertThat(order.items.get(1).quantity).isEqualTo(2);
        }
    }
}