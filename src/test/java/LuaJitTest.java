import party.iroiro.luajava.luajit.LuaJit;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class LuaJitTest {

    public static void main(String[] args) throws IOException {
        LuaJit j = new LuaJit();
        j.load("print(\"Hello World!\")");
        ByteBuffer code = j.dump();

        File byteCodeFile = new File("helloWord.luac");
        if (!byteCodeFile.exists()) byteCodeFile.createNewFile();

        byte[] bytes = new byte[code.remaining()];
        code.get(bytes);

        FileOutputStream fos = new FileOutputStream(byteCodeFile);
        fos.write(bytes);
        fos.close();

        System.out.println();
        j.close();
    }

}
