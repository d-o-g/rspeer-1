package org.rspeer.runetek.api.input.menu.tree;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.input.menu.ContextMenu;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.SceneObjects;

public final class ObjectAction extends Action {

    private final Position position;

    public ObjectAction(int opcode, int uid, int sceneX, int sceneY) {
        super(opcode, uid, sceneX, sceneY);
        position = new Position(sceneX, sceneY).getPosition();
    }

    public int getId() {
        return primary;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(super.toString())
                .append("[x=")
                .append(position.getX())
                .append(",y=")
                .append(position.getY());
        SceneObject obj = getSource();
        if (obj != null) {
            builder.append(",id=")
                    .append(obj.getId())
                    .append(",type=")
                    .append(obj.getType())
                    .append(",orientation=")
                    .append(obj.getOrientation());
        }
        return builder.append("]").toString();
    }

    public SceneObject getSource() {
        return SceneObjects.getByUid(ContextMenu.compileUid(secondary, tertiary, 2, false, primary));
    }
}
