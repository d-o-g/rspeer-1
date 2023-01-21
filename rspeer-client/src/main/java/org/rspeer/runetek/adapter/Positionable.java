package org.rspeer.runetek.adapter;

import org.rspeer.runetek.api.commons.math.Distance;
import org.rspeer.runetek.api.commons.math.DistanceEvaluator;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.FinePosition;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.movement.position.ScenePosition;
import org.rspeer.runetek.api.movement.position.ScreenPosition;
import org.rspeer.runetek.api.scene.Players;

import java.text.FieldPosition;

public interface Positionable extends Comparable<Positionable> {

    default int getX() {
        return getPosition().getX();
    }

    default int getY() {
        return getPosition().getY();
    }

    /**
     * @see Position
     * @return The position of this object, relative to the world
     */
    Position getPosition();

    default ScenePosition toScene(){
        return getPosition().toScene();
    }

    default FinePosition toFine(){
        return toScene().toFine();
    }

    default ScreenPosition toScreen(){
        return toFine().toScreen();
    }

    /**
     * @return The distance between this and the local player
     */
    default double distance() {
        return Distance.between(this, Players.getLocal());
    }

    /**
     * @param other The other positionable
     * @return The distance between this and another positionable
     */
    default double distance(Positionable other) {
        return Distance.between(this, other);
    }

    default double distance(DistanceEvaluator evaluator) {
        return Distance.evaluate(evaluator, this, Players.getLocal());
    }

    default double distance(Positionable other, DistanceEvaluator evaluator) {
        return Distance.evaluate(evaluator, this, other);
    }

    /**
     * @return The floor level
     */
    default int getFloorLevel() {
        return getPosition().getFloorLevel();
    }

    @Override
    default int compareTo(Positionable o){
        return (int) Distance.between(this, o);
    }

    default boolean isPositionWalkable(){
        return Movement.isWalkable(this);
    }

    default boolean isPositionInteractable(){
        return Movement.isInteractable(this);
    }
}
