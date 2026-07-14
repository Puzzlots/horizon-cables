package me.zombii.horizon.common.wired.network;

import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.*;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class NetworkGroup<T extends AbstractNetwork> {

    private final ObjectList<T> networks;
    private final Int2ObjectMap<T> idToNetwork;
    private final Object2IntMap<T> networkToId;
    private final AtomicInteger networkCount;
    private final IntList freedSlots;
    private final Supplier<T> networkMaker;

    public NetworkGroup(Supplier<T> networkMaker) {
        this.networks = ObjectLists.synchronize(new ObjectArrayList<>());

        this.idToNetwork = Int2ObjectMaps.synchronize(new Int2ObjectArrayMap<>());
        this.networkToId = Object2IntMaps.synchronize(new Object2IntArrayMap<>());
        this.freedSlots = IntLists.synchronize(new IntArrayList());

        this.networkCount = new AtomicInteger(0);
        this.networkMaker = networkMaker;
    }

    public List<T> getNetworks() {
        return networks;
    }

    public T newNetwork() {
        T network = networkMaker.get();
        add(network);
        return network;
    }

    public int add(T network) {
//        int id = !freedSlots.isEmpty() ? freedSlots.dequeueInt() : networkCount.getAndIncrement();
        int id = 0;
        System.out.println(freedSlots.size());
        if (!freedSlots.isEmpty()) {
            id = freedSlots.removeFirst();
            System.out.println("Reusing ID: " + id);
        } else {
            id = networkCount.getAndIncrement();
            System.out.println("Creating ID: " + id);
        }

        networks.add(network);
        idToNetwork.put(id, network);
        networkToId.put(network, id);

        network.setGroup(this);
        network.setNetworkID(id);

        return id;
    }

    public T get(int id) {
        return idToNetwork.get(id);
    }

    public T remove(int id) {
        System.out.println("Removing network: " + get(id));
        if (id == networkCount.get() - 1) networkCount.decrementAndGet();
        else freedSlots.add(id);

        T network = idToNetwork.get(id);
        networks.remove(network);
        idToNetwork.remove(id, network);
        networkToId.remove(network, id);
        network.setGroup(null);

        return network;
    }

    public boolean inGroup(T network) {
        return networkToId.containsKey(network);
    }

    public void save(JsonObject groupObject) {
        groupObject.set("networkCount", networkCount.get());
        JsonArray freedSlotsQueue = new JsonArray();
        for (Integer freedSlot : freedSlots) {
            freedSlotsQueue.add(freedSlot);
        }
        groupObject.set("freedSlots", freedSlotsQueue);
        JsonObject networks = new JsonObject();
        for (T network : this.networks) {
            JsonObject networkObject = new JsonObject();
            network.save(networkObject);

            networks.add(String.valueOf(network.getNetworkID()), networkObject);
        }
        groupObject.set("networks", networks);
    }

    public void load(JsonObject groupObject) {
        networkCount.set(groupObject.get("networkCount").asInt());
        JsonArray freedSlotsQueue = groupObject.get("freedSlots").asArray();
        for (JsonValue value : freedSlotsQueue) {
            freedSlots.add(value.asInt());
        }
        JsonObject networks = groupObject.get("networks").asObject();
        for (JsonObject.Member value : networks) {
            JsonObject networkObject = value.getValue().asObject();

            int networkID = Integer.parseInt(value.getName());
            T network = networkMaker.get();
            network.setGroup(this);
            network.setNetworkID(networkID);
            network.load(networkObject);

            this.networks.add(network);
            idToNetwork.put(networkID, network);
            networkToId.put(network, networkID);
            System.out.println("Adding network: " + network);
        }
    }

    public void remove(T network) {
        remove(network.getNetworkID());
    }
}
