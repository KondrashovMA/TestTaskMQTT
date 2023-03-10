import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.pet.taskMQTT.TaskMQTTapp;
import ru.pet.taskMQTT.domain.sensors.model.SignalizationDto;
import ru.pet.taskMQTT.domain.sensors.mosquitto.util.DetectorDeserializer;
import ru.pet.taskMQTT.domain.sensors.mosquitto.util.SignalizationJsonSerializer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = TaskMQTTapp.class)
public class UtilsTest {
    @Autowired
    SignalizationJsonSerializer signalizationJsonSerializer;

    @Autowired
    DetectorDeserializer detectorDeserializer;

    @Test
    public void signalizationJsonTest(){
        SignalizationDto signalizationDto = new SignalizationDto();
        signalizationDto.setDevice("device");
        signalizationDto.setSensor("sensor");
        signalizationDto.setRepeated(3);
        signalizationDto.setUntis("grades");
        signalizationDto.setValue(50);

        String resultJson = "{\"device\":\"device\",\"sensor\":\"sensor\",\"value\":50,\"untis\":\"grades\",\"repeated\":3}";

        var result = signalizationJsonSerializer.serializeToJson(signalizationDto);
        assertThat(result).isEqualTo(resultJson);

        signalizationDto.setDevice("/room/dev");
        signalizationDto.setSensor("sensor/light");
        signalizationDto.setRepeated(5);
        signalizationDto.setUntis("lux");
        signalizationDto.setValue(500);

        resultJson = "{\"device\":\"/room/dev\",\"sensor\":\"sensor/light\",\"value\":500,\"untis\":\"lux\",\"repeated\":5}";
        result = signalizationJsonSerializer.serializeToJson(signalizationDto);

        assertThat(result).isEqualTo(resultJson);
    }

    @Test
    public void testParseJson(){
        String json = "{\"detecorName\":\"/room/device1\",\"sensorsValues\":{\"light\":500,\"fire\":300}}";
        var res = detectorDeserializer.deserialize(json);
        System.out.println(res);

        json = "{\"detecorName\":\"/room/device1\",\"sensorsValues\":{\"light\":500,\"fire\":300,\"door\":\"open\"}}";
        res = detectorDeserializer.deserialize(json);
        System.out.println(res);
    }
}