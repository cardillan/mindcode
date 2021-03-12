package info.teksol.mindcode;

import java.util.Objects;

public class SensorReading implements AstNode {
    private final String target;
    private final String sensor;

    public SensorReading(String target, String sensor) {
        this.target = target;
        this.sensor = sensor;
    }

    public String getTarget() {
        return target;
    }

    public String getSensor() {
        return sensor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SensorReading that = (SensorReading) o;
        return Objects.equals(target, that.target) &&
                Objects.equals(sensor, that.sensor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(target, sensor);
    }

    @Override
    public String toString() {
        return "SensorReading{" +
                "target='" + target + '\'' +
                ", sensor='" + sensor + '\'' +
                '}';
    }
}
