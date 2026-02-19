package com.royalopulence.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.royalopulence.model.core.Room;
import com.royalopulence.model.core.RoomType;
import com.royalopulence.repository.RoomRepository;
import com.royalopulence.repository.RoomTypeRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoomSeeder implements CommandLineRunner {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    // change if you want more/less
    private static final int ROOMS_PER_TYPE = 50;

    @Override
    public void run(String... args) {

        List<RoomType> types = roomTypeRepository.findAll();
        if (types.isEmpty()) {
            System.out.println("⚠️ RoomSeeder: No room types found. Skipping seeding rooms.");
            return;
        }

        List<Room> toInsert = new ArrayList<>();

        for (RoomType type : types) {

            long existingCount = roomRepository.countByRoomTypeId(type.getId());
            if (existingCount >= ROOMS_PER_TYPE) {
                System.out.println("✅ RoomSeeder: Rooms already exist for " + type.getName()
                        + " (" + existingCount + "), skipping.");
                continue;
            }

            int start = (int) existingCount + 1;
            int needed = ROOMS_PER_TYPE - (int) existingCount;

            for (int i = 0; i < needed; i++) {
                int roomNumber = start + i;

                Room r = new Room();
                r.setRoomTypeId(type.getId());
                r.setStatus("AVAILABLE");

                // Optional fields (only set if your Room model has them)
                try { r.getClass().getMethod("setRoomNumber", String.class).invoke(r, type.getName().substring(0, 1).toUpperCase() + String.format("%03d", roomNumber)); } catch (Exception ignored) {}
                try { r.getClass().getMethod("setFloor", Integer.class).invoke(r, (roomNumber % 10) + 1); } catch (Exception ignored) {}

                toInsert.add(r);
            }

            System.out.println("🌱 RoomSeeder: Will insert " + needed + " rooms for " + type.getName());
        }

        if (!toInsert.isEmpty()) {
            roomRepository.saveAll(toInsert);
            System.out.println("✅ RoomSeeder: Inserted total rooms = " + toInsert.size());
        } else {
            System.out.println("✅ RoomSeeder: Nothing to insert.");
        }
    }
}
