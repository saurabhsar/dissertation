package resource;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class RequestModel {
    int threads;
    int load;
    int timeInMilis;
    boolean transactional;
    boolean durable;
    RequestType requestType;
}
