package org.rspeer.runetek.api.movement.global.augmenting;

import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Digraph;
import org.rspeer.runetek.api.component.tab.Magic;
import org.rspeer.runetek.api.component.tab.Spell;
import org.rspeer.runetek.api.movement.global.Mapo;
import org.rspeer.runetek.api.movement.global.PathNode;
import org.rspeer.runetek.api.movement.position.Position;

import java.util.ArrayList;
import java.util.List;

public final class DirectTeleportProvider implements TeleportProvider {

    @Override
    public void to(Mapo map) {
        Digraph<PathNode, PathNode> graph = map.getGraph();
        for (PathNode node : graph) {
            for (PathNode teleport : create()) {
                PathNode link = map.getNearestTo(teleport.getPosition());
                graph.addVertex(teleport);
                graph.addEdge(link, teleport);
                graph.addEdge(node, teleport);
            }
        }
    }

    private List<PathNode> create() {
        List<PathNode> nodes = new ArrayList<>();
        nodes.addAll(createModernTeleports());
        nodes.addAll(createAncientTeleports());
        nodes.addAll(createLunarTeleports());
        nodes.addAll(createScrollTeleports());
        nodes.addAll(createJewelleryTeleports());

        //TODO add IndirectTeleportNodes like fairy rings, charter ships, spirit trees
        //just add them as edge to nodes close to fairy rings etc

        //ring of wealth miscellania and keldagrim (with misc and between a rock varps)
        //skills necklace and add conditional positions? e.g. skills necklace teleports inside if 45 farm, outside if not
        //skillcape teleports, slayer ring, digsite pendant, burning amulet, xerics talisman
        //ectophial, mythical cape, royal seed pod, enchanted lyre, elf teleport crystals
        //wilderness obelisks, diary items

        return nodes;
    }

    private DirectTeleportNode modern(int x, int y, Spell.Modern spell) {
        return new DirectTeleportNode(new Position(x, y)).spell(Magic.Book.MODERN, spell);
    }

    private DirectTeleportNode ancient(int x, int y, Spell.Ancient spell) {
        return new DirectTeleportNode(new Position(x, y)).spell(Magic.Book.ANCIENT, spell);
    }

    private DirectTeleportNode lunar(int x, int y, Spell.Lunar spell) {
        return new DirectTeleportNode(new Position(x, y)).spell(Magic.Book.LUNAR, spell);
    }

    private List<PathNode> createModernTeleports() {
        List<PathNode> teleports = new ArrayList<>();

        teleports.add(modern(3222, 3219, Spell.Modern.HOME_TELEPORT));
        teleports.add(modern(3212, 3428, Spell.Modern.VARROCK_TELEPORT).tablet(8007));
        teleports.add(modern(3225, 3219, Spell.Modern.LUMBRIDGE_TELEPORT).tablet(8008));
        teleports.add(modern(2966, 3379, Spell.Modern.FALADOR_TELEPORT).tablet(8009));
        teleports.add(modern(2757, 3479, Spell.Modern.CAMELOT_TELEPORT).tablet(8010));
        teleports.add(modern(2664, 3306, Spell.Modern.ARDOUGNE_TELEPORT).condition(() -> Varps.get(165) > 28));
        teleports.add(modern(2547, 3114, Spell.Modern.WATCHTOWER_TELEPORT).condition(() -> Varps.get(212) > 12));
        teleports.add(modern(2891, 3678, Spell.Modern.TROLLHEIM_TELEPORT).condition(() -> Varps.get(335) == 110));

        //TODO dynamic positions e.g. varrock can be changed to ge (based on diary completion?)
        //TODO rfd miniquest for ape atoll tele, incantation for kourend (maybe varp?)

        return teleports;
    }

    private List<PathNode> createAncientTeleports() {
        List<PathNode> teleports = new ArrayList<>();
        teleports.add(ancient(3087, 3496, Spell.Ancient.ANCIENT_HOME_TELEPORT));
        teleports.add(ancient(3097, 9880, Spell.Ancient.PADDEWWA_TELEPORT).tablet(12781));
        teleports.add(ancient(3319, 3336, Spell.Ancient.SENNTISTEN_TELEPORT).tablet(12782));
        teleports.add(ancient(3494, 3473, Spell.Ancient.KHARYRLL_TELEPORT).tablet(12779));
        teleports.add(ancient(3002, 3472, Spell.Ancient.LASSAR_TELEPORT).tablet(12780));
        teleports.add(ancient(2969, 3695, Spell.Ancient.DAREEYAK_TELEPORT).tablet(12777).wilderness());
        teleports.add(ancient(3157, 3667, Spell.Ancient.CARRALLANGAR_TELEPORT).tablet(12776).wilderness());
        teleports.add(ancient(3288, 3888, Spell.Ancient.ANNAKARL_TELEPORT).tablet(12775).wilderness());
        teleports.add(ancient(2977, 3872, Spell.Ancient.GHORROCK_TELEPORT).tablet(12778).wilderness());
        return teleports;
    }

    private List<PathNode> createLunarTeleports() {
        List<PathNode> teleports = new ArrayList<>();
        //TODO ourania after finding out if its unlocked
        teleports.add(lunar(3087, 3496, Spell.Lunar.LUNAR_HOME_TELEPORT));
        teleports.add(lunar(2113, 3915, Spell.Lunar.MOONCLAN_TELEPORT));
        teleports.add(lunar(2546, 3755, Spell.Lunar.WATERBIRTH_TELEPORT));
        teleports.add(lunar(2543, 3568, Spell.Lunar.BARBARIAN_TELEPORT));
        teleports.add(lunar(2636, 3167, Spell.Lunar.KHAZARD_TELEPORT));
        teleports.add(lunar(2612, 3391, Spell.Lunar.FISHING_GUILD_TELEPORT));
        teleports.add(lunar(2802, 3449, Spell.Lunar.CATHERBY_TELEPORT));
        teleports.add(lunar(2973, 3939, Spell.Lunar.ICE_PLATEAU_TELEPORT).wilderness());
        return teleports;
    }


    private List<PathNode> createScrollTeleports() {
        List<PathNode> teleports = new ArrayList<>();
        teleports.add(new DirectTeleportNode(new Position(1645, 3579)).tablet(12408)); //Piscatoris
        teleports.add(new DirectTeleportNode(new Position(2339, 3649)).tablet(23387)); //Watson
        teleports.add(new DirectTeleportNode(new Position(3421, 2917)).tablet(12402)); //Nardah
        teleports.add(new DirectTeleportNode(new Position(3324, 3412)).tablet(12403)); //Digsite
        teleports.add(new DirectTeleportNode(new Position(2542, 2925)).tablet(12404)); //Feldip hills
        teleports.add(new DirectTeleportNode(new Position(2657, 2660)).tablet(12407)); //Pest control
        teleports.add(new DirectTeleportNode(new Position(2788, 3066)).tablet(12409)); //Tai bwo wannai
        teleports.add(new DirectTeleportNode(new Position(3303, 3487)).tablet(12642)); //Lumberyard
        return teleports;
    }

    private List<PathNode> createJewelleryTeleports() {
        List<PathNode> teleports = new ArrayList<>();

        teleports.add(new DirectTeleportNode(new Position(3161, 3478))
                .item("(?i)ring of wealth.?\\(.+", "(?i)grand exchange"));
        teleports.add(new DirectTeleportNode(new Position(2994, 3377))
                .item("(?i)ring of wealth.?\\(.+", "(?i)falador.*"));

        teleports.add(new DirectTeleportNode(new Position(3313, 3233))
                .item("(?i)ring of dueling.?\\(.+", "(?i).*duel arena.*"));
        teleports.add(new DirectTeleportNode(new Position(2440, 3090))
                .item("(?i)ring of dueling.?\\(.+", "(?i).*castle wars.*"));
        teleports.add(new DirectTeleportNode(new Position(3388, 3161))
                .item("(?i)ring of dueling.?\\(.+", "(?i).*clan wars.*"));

        teleports.add(new DirectTeleportNode(new Position(3113, 3179))
                .item("(?i)necklace of passage.?\\(.+", "(?i).*wizard.+tower.*"));
        teleports.add(new DirectTeleportNode(new Position(2430, 3347))
                .item("(?i)necklace of passage.?\\(.+", "(?i).*the.+outpost.*"));
        teleports.add(new DirectTeleportNode(new Position(3406, 3156))
                .item("(?i)necklace of passage.?\\(.+", "(?i).*eagl.+eyrie.*"));

        teleports.add(new DirectTeleportNode(new Position(2882, 3550))
                .item("(?i)combat brace.+\\(.+", "(?i).*warrior.+guild.*"));
        teleports.add(new DirectTeleportNode(new Position(3190, 3366))
                .item("(?i)combat brace.+\\(.+", "(?i).*champion.+guild.*"));
        teleports.add(new DirectTeleportNode(new Position(3053, 3486))
                .item("(?i)combat brace.+\\(.+", "(?i).*monastery.*"));
        teleports.add(new DirectTeleportNode(new Position(2656, 3442))
                .item("(?i)combat brace.+\\(.+", "(?i).*rang.+guild.*"));

        teleports.add(new DirectTeleportNode(new Position(3087, 3496))
                .item("(?i).+glory.*\\(.+", "(?i).*edgeville.*"));
        teleports.add(new DirectTeleportNode(new Position(2918, 3176))
                .item("(?i).+glory.*\\(.+", "(?i).*karamja.*"));
        teleports.add(new DirectTeleportNode(new Position(3105, 3251))
                .item("(?i).+glory.*\\(.+", "(?i).*draynor.*"));
        teleports.add(new DirectTeleportNode(new Position(3293, 3163))
                .item("(?i).+glory.*\\(.+", "(?i).*al kharid.*"));

        teleports.add(new DirectTeleportNode(new Position(2897, 3551))
                .item("(?i)game.+neck.+\\(.+", "(?i).*burthorpe.*"));
        teleports.add(new DirectTeleportNode(new Position(2520, 3570))
                .item("(?i)game.+neck.+\\(.+", "(?i).*barbarian.*"));
        teleports.add(new DirectTeleportNode(new Position(2965, 4832, 2))
                .item("(?i)game.+neck.+\\(.+", "(?i).*corporeal.*"));
        teleports.add(new DirectTeleportNode(new Position(1623, 3937))
                .item("(?i)game.+neck.+\\(.+", "(?i).*wintertodt.*"));

        return teleports;
    }
}
