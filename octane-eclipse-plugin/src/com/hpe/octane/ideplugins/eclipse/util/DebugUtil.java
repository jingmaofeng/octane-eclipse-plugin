package com.hpe.octane.ideplugins.eclipse.util;

import java.util.Collection;
import java.util.stream.Collectors;

import com.hpe.adm.nga.sdk.model.EntityModel;
import com.hpe.adm.octane.services.filtering.Entity;
import com.hpe.adm.octane.services.mywork.MyWorkUtil;

public class DebugUtil {

    public static void printEntities(Collection<EntityModel> entities) {
        System.out.println("My Work Entities size: " + entities.size());
        if (entities.size() != 0) {
            String entitiesString = entities
                    .stream()
                    .map(MyWorkUtil::getEntityModelFromUserItem)
                    .map(em -> {
                        if (em.getValue("name") != null) {
                            return em.getValue("name").getValue().toString();
                        } else if (Entity.COMMENT == Entity.getEntityType(em)) {
                            return "(Comment)";
                        }
                        return "{???}";
                    })
                    .collect(Collectors.joining(", "));
            System.out.println("My Work Entities: " + entitiesString);
        }
    }

}
