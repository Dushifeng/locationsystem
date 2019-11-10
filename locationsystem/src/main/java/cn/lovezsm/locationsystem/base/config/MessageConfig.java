package cn.lovezsm.locationsystem.base.config;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Component
public class MessageConfig{
    private Set<String> macAllow;
    private Set<String> macRefuse;
    public MessageConfig() {
        macAllow = new HashSet<>();
        macRefuse = new HashSet<>();
    }
    private List<Integer> frequencyRefuse;
}
