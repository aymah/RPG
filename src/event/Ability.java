package event;

public class Ability extends GenericMenuItem {

	public Ability(String name) {
		super(name);
	}

	@Override
	public String getName() {
		return name;
	}
}
