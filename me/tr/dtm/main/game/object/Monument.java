package me.tr.dtm.main.game.object;

import me.tr.dtm.main.game.Game;
import me.tr.dtm.main.game.Team;
import org.bukkit.Location;

public class Monument {

    private Team team;
    private Location location;

    public void Monument(Team team, Location location) {

        this.team = team;
        this.location = location;

    }

    public Location getLocation() {
        return location;
    }

    public Team getTeam() {
        return team;
    }
}
