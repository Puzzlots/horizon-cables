import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args) throws IOException {
        File file = new File("E:\\horizon\\src\\common\\resources\\assets\\horizon\\models\\pipes\\solid\\");
        File[] files = file.listFiles();

        for (File listFile : files) {
            FileInputStream fis = new FileInputStream(listFile);
            byte[] bytes = fis.readAllBytes();
            fis.close();

            String data = new String(bytes);
            JsonObject ourFile = JsonValue.readHjson(data).asObject();
//            ourFile.get("textures").asObject().get("layout-pipe.png").asObject().set("fileName", "horizon:textures/pipes/pipe-glass.png");
            ourFile.get("textures").asObject().get("pipe-white.png").asObject().set("fileName", "horizon:textures/pipes/pipe-white.png");
            ourFile.set("isTransparent", true);
            JsonArray value = ourFile.get("cuboids").asArray();
            for (JsonValue jsonValue : value) {
                JsonObject cuboid = jsonValue.asObject();
                JsonObject faces = cuboid.get("faces").asObject();
                Queue<String> toRemove = new LinkedList<>();
                for (JsonObject.Member face : faces) {
                    JsonObject faceObject = face.getValue().asObject();
                    JsonArray uvs = faceObject.get("uv").asArray();
                    if (uvs.get(0).asInt() == 15 && uvs.get(1).asInt() == 15 && uvs.get(2).asInt() == 16 && uvs.get(3).asInt() == 16) {
                        toRemove.add(face.getName());
                    }
                }
                System.out.println(listFile);
                while (!toRemove.isEmpty()) {
                    String name = toRemove.poll();
                    faces.remove(name);
                    System.out.println("Removing " + name);
                }
            }
            byte[] newBytes = ourFile.toString(Stringify.PLAIN).getBytes();
            FileOutputStream outputStream = new FileOutputStream(listFile);
            outputStream.write(newBytes);
            outputStream.close();
        }
    }

}
