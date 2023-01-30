package com.motudy.modules.account;

import com.motudy.modules.tag.Tag;
import com.motudy.modules.zone.Zone;
import com.querydsl.core.types.Predicate;

import java.util.Set;

public class AccountPredicates {
    public static Predicate findByTagsAndZones(Set<Tag> tags, Set<Zone> zones) {
        /* Account가 들고 있는 zone중에 아무거나 해당되는지
         그리고 Account가 들고 있는 tags중 아무거나 해당되는지 리턴
         */
        QAccount account = QAccount.account;
        return account.zones.any().in(zones).and(account.tags.any().in(tags));
    }
}
