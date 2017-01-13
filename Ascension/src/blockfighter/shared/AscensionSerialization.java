package blockfighter.shared;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage.DiscoverHost;
import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive;
import com.esotericsoftware.kryonet.FrameworkMessage.Ping;
import com.esotericsoftware.kryonet.FrameworkMessage.RegisterTCP;
import com.esotericsoftware.kryonet.FrameworkMessage.RegisterUDP;
import com.esotericsoftware.kryonet.Serialization;
import java.nio.ByteBuffer;

public class AscensionSerialization implements Serialization {

    private final KryoFactory factory = () -> {
        Kryo kryo = new Kryo();
        kryo.register(RegisterTCP.class);
        kryo.register(RegisterUDP.class);
        kryo.register(KeepAlive.class);
        kryo.register(DiscoverHost.class);
        kryo.register(Ping.class);
        kryo.setReferences(false);
        kryo.setRegistrationRequired(true);

        kryo.register(byte[].class);
        kryo.register(byte[][].class);

        return kryo;
    };
    private final KryoPool kryoPool;

    public AscensionSerialization() {
        kryoPool = new KryoPool.Builder(factory).softReferences().build();
    }

    @Override
    public int getLengthLength() {
        return 4;
    }

    @Override
    public Object read(Connection connection, ByteBuffer buffer) {
        ByteBufferInput input = new ByteBufferInput();
        Kryo kryo = kryoPool.borrow();
        input.setBuffer(buffer);
        kryo.getContext().put("connection", connection);
        Object obj = kryo.readClassAndObject(input);
        kryoPool.release(kryo);
        return obj;
    }

    @Override
    public int readLength(ByteBuffer buffer) {
        return buffer.getInt();
    }

    @Override
    public void write(Connection connection, ByteBuffer buffer, Object object) {
        ByteBufferOutput output = new ByteBufferOutput();
        Kryo kryo = kryoPool.borrow();
        output.setBuffer(buffer);
        kryo.getContext().put("connection", connection);
        kryo.writeClassAndObject(output, object);
        output.flush();
        kryoPool.release(kryo);
    }

    @Override
    public void writeLength(ByteBuffer buffer, int length) {
        buffer.putInt(length);
    }

}
