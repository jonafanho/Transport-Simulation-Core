package org.mtr.core.data;

import it.unimi.dsi.fastutil.doubles.DoubleImmutableList;
import org.msgpack.core.MessagePacker;
import org.mtr.core.tools.Utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VehicleCar extends SerializedDataBase {

	public final String vehicleId;
	public final double length;
	public final double width;
	public final double bogie1Position;
	public final double bogie2Position;
	public final boolean hasOneBogie;
	public final DoubleImmutableList doorLeftPositions;
	public final DoubleImmutableList doorRightPositions;

	private static final String KEY_VEHICLE_ID = "vehicle_id";
	private static final String KEY_LENGTH = "length";
	private static final String KEY_WIDTH = "width";
	private static final String KEY_BOGIE_1_POSITION = "bogie_1_position";
	private static final String KEY_BOGIE_2_POSITION = "bogie_2_position";
	private static final String KEY_DOOR_LEFT_POSITIONS = "door_left_positions";
	private static final String KEY_DOOR_RIGHT_POSITIONS = "door_right_positions";

	public VehicleCar(String vehicleId, double length, double width, double bogie1Position, double bogie2Position, double[] doorLeftPositions, double[] doorRightPositions) {
		this.vehicleId = vehicleId;
		this.length = length;
		this.width = width;
		this.bogie1Position = Utilities.clamp(bogie1Position, 0, length);
		this.bogie2Position = Utilities.clamp(bogie2Position, 0, length);
		hasOneBogie = this.bogie1Position == this.bogie2Position;
		this.doorLeftPositions = new DoubleImmutableList(doorLeftPositions);
		this.doorRightPositions = new DoubleImmutableList(doorRightPositions);
	}

	public VehicleCar(MessagePackHelper messagePackHelper) {
		vehicleId = messagePackHelper.getString(KEY_VEHICLE_ID, "");
		length = messagePackHelper.getDouble(KEY_LENGTH, 0);
		width = messagePackHelper.getDouble(KEY_WIDTH, 0);
		bogie1Position = messagePackHelper.getDouble(KEY_BOGIE_1_POSITION, 0);
		bogie2Position = messagePackHelper.getDouble(KEY_BOGIE_2_POSITION, 0);
		hasOneBogie = bogie1Position == bogie2Position;
		doorLeftPositions = getDoorPositions(messagePackHelper, KEY_DOOR_LEFT_POSITIONS);
		doorRightPositions = getDoorPositions(messagePackHelper, KEY_DOOR_RIGHT_POSITIONS);
	}

	@Override
	public void updateData(MessagePackHelper messagePackHelper) {
	}

	@Override
	public void toMessagePack(MessagePacker messagePacker) throws IOException {
		messagePacker.packString(KEY_VEHICLE_ID).packString(vehicleId);
		messagePacker.packString(KEY_LENGTH).packDouble(length);
		messagePacker.packString(KEY_WIDTH).packDouble(width);
		messagePacker.packString(KEY_BOGIE_1_POSITION).packDouble(bogie1Position);
		messagePacker.packString(KEY_BOGIE_2_POSITION).packDouble(bogie2Position);
		messagePacker.packString(KEY_DOOR_LEFT_POSITIONS).packArrayHeader(doorLeftPositions.size());
		for (final double doorPosition : doorLeftPositions) {
			messagePacker.packDouble(doorPosition);
		}
		messagePacker.packString(KEY_DOOR_RIGHT_POSITIONS).packArrayHeader(doorRightPositions.size());
		for (final double doorPosition : doorRightPositions) {
			messagePacker.packDouble(doorPosition);
		}
	}

	@Override
	public int messagePackLength() {
		return 7;
	}

	private static DoubleImmutableList getDoorPositions(MessagePackHelper messagePackHelper, String key) {
		final List<Double> tempDoorPositions = new ArrayList<>();
		messagePackHelper.iterateArrayValue(key, value -> tempDoorPositions.add(value.asFloatValue().toDouble()));
		return new DoubleImmutableList(tempDoorPositions);
	}
}
