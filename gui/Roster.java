package com.dyn.mentor.gui;

import java.awt.Color;
import java.util.ArrayList;

import com.dyn.DYNServerConstants;
import com.dyn.DYNServerMod;
import com.dyn.server.http.GetProgramRoster;
import com.dyn.server.http.GetPrograms;
import com.dyn.server.http.GetScheduledPrograms;
import com.dyn.server.keys.KeyManager;
import com.dyn.server.utils.BooleanChangeListener;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.rabbit.gui.background.DefaultBackground;
import com.rabbit.gui.component.control.DropDown;
import com.rabbit.gui.component.control.PictureButton;
import com.rabbit.gui.component.display.Picture;
import com.rabbit.gui.component.display.TextLabel;
import com.rabbit.gui.component.list.ScrollableDisplayList;
import com.rabbit.gui.component.list.entries.ListEntry;
import com.rabbit.gui.component.list.entries.StringEntry;
import com.rabbit.gui.render.TextAlignment;
import com.rabbit.gui.show.Show;

public class Roster extends Show {

	private DropDown<Integer> orgs;
	private DropDown<Integer> programs;
	private DropDown<Integer> scheduledProg;
	private ScrollableDisplayList rosterDisplayList;
	TextLabel numberOfStudentsOnRoster;

	int selectedOrg;
	int selectedProgram;

	public Roster() {
		setBackground(new DefaultBackground());
		title = "Mentor Gui Roster Management";
	}

	// List<Pair<String, Integer>> content = new ArrayList<Pair<String,
	// Integer>>();
	// content.add(new Pair<String, Integer>("Test", 1));
	// refreshDropdown("program", content);

	private void dropdownSelected(DropDown<Integer> dropdown, String selected) {
		System.out.println("Selected: " + selected + " " + dropdown.getElement(selected).getValue());

		if (dropdown.getId().equals("org")) {
			selectedOrg = dropdown.getElement(selected).getValue();
			selectedProgram = 0;
			programs.clear();
			scheduledProg.clear();
			rosterDisplayList.clear();

			GetPrograms progRequest = new GetPrograms(dropdown.getElement(selected).getValue(),
					KeyManager.getSecretKey(dropdown.getElement(selected).getValue()),
					KeyManager.getOrgKey(dropdown.getElement(selected).getValue()));

			BooleanChangeListener listener = event -> {
				if (event.getDispatcher().getFlag()) {
					JsonObject jObj = progRequest.jsonResponse.getAsJsonObject();
					if (jObj.has("result")) {
						for (JsonElement entry : jObj.get("result").getAsJsonArray()) {
							JsonObject entryObj = entry.getAsJsonObject();
							if (entryObj.has("id") && entryObj.has("name")) {
								programs.add(entryObj.get("name").getAsString(), entryObj.get("id").getAsInt());
							}
						}
						// if(!programs.isEmpty()){
						// programs.setIsEnabled(true);
						// }
					}
				}
			};

			progRequest.responseReceived.addBooleanChangeListener(listener);

		} else if (dropdown.getId().equals("program")) {

			scheduledProg.clear();
			rosterDisplayList.clear();

			GetScheduledPrograms scheduleRequest = new GetScheduledPrograms(selectedOrg,
					dropdown.getElement(selected).getValue(), KeyManager.getSecretKey(selectedOrg),
					KeyManager.getOrgKey(selectedOrg));

			selectedProgram = dropdown.getElement(selected).getValue();

			BooleanChangeListener listener = event -> {
				if (event.getDispatcher().getFlag()) {
					System.out.println("Response Recieved");
					JsonObject jObj = scheduleRequest.jsonResponse.getAsJsonObject();
					if (jObj.has("result")) {
						for (JsonElement entry : jObj.get("result").getAsJsonArray()) {
							JsonObject entryObj = entry.getAsJsonObject();
							if (entryObj.has("id") && entryObj.has("name")) {
								System.out.println("adding: " + entryObj.get("name").getAsString());
								scheduledProg.add(entryObj.get("name").getAsString(), entryObj.get("id").getAsInt());
							}
						}
						// if(!scheduledProg.isEmpty()){
						// scheduledProg.setIsEnabled(true);
						// }
					}
				}
			};

			scheduleRequest.responseReceived.addBooleanChangeListener(listener);

		} else if (dropdown.getId().equals("schedule")) {

			rosterDisplayList.clear();

			GetProgramRoster rosterRequest = new GetProgramRoster(selectedOrg, selectedProgram,
					dropdown.getElement(selected).getValue(), KeyManager.getSecretKey(selectedOrg),
					KeyManager.getOrgKey(selectedOrg));

			BooleanChangeListener listener = event -> {
				if (event.getDispatcher().getFlag()) {
					System.out.println("Response Recieved");
					JsonObject jObj = rosterRequest.jsonResponse.getAsJsonObject();
					if (jObj.has("result")) {
						rosterDisplayList.add(new StringEntry("Student Name : CCOL Name"));
						rosterDisplayList.add(new StringEntry("------------------------------------------"));
						for (JsonElement entry : jObj.get("result").getAsJsonArray()) {
							JsonObject entryObj = entry.getAsJsonObject();
							JsonObject userEntryObj = entryObj.get("user").getAsJsonObject();
							if (userEntryObj.has("username") && userEntryObj.has("full_name")) {
								rosterDisplayList.add(new StringEntry(String.format("%s : %s",
										(userEntryObj.get("full_name").isJsonNull()) ? "Unavailable"
												: userEntryObj.get("full_name").getAsString(),
										userEntryObj.get("username").getAsString())));
							}
						}
						numberOfStudentsOnRoster
								.setText("Roster Count: " + (rosterDisplayList.getContent().size() - 2));
					}
				}
			};

			rosterRequest.responseReceived.addBooleanChangeListener(listener);
		}
	}

	@Override
	public void setup() {
		super.setup();

		registerComponent(
				new TextLabel((int) (width * .15), (int) (height * .2), (int) (width / 3.3), 20, Color.black, "Orgs"));

		orgs = new DropDown<Integer>((int) (width * .15), (int) (height * .25), (int) (width / 3.3), 20).add("DYN", 42)
				.setId("org").setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(orgs);

		registerComponent(new TextLabel((int) (width * .15), (int) (height * .35), (int) (width / 3.3), 20, Color.black,
				"Programs"));

		programs = new DropDown<Integer>((int) (width * .15), (int) (height * .40), (int) (width / 3.3), 20)
				.setId("program").setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(programs);

		registerComponent(new TextLabel((int) (width * .15), (int) (height * .5), (int) (width / 3.3), 20, Color.black,
				"Scheduled Programs"));

		scheduledProg = new DropDown<Integer>((int) (width * .15), (int) (height * .55), (int) (width / 3.3), 20)
				.setId("schedule").setItemSelectedListener((DropDown<Integer> dropdown, String selected) -> {
					dropdownSelected(dropdown, selected);
				});

		registerComponent(scheduledProg);

		registerComponent(new TextLabel(width / 3, (int) (height * .1), width / 3, 20, "Roster Management",
				TextAlignment.CENTER));

		// The students on the Roster List for this class
		ArrayList<ListEntry> rlist = new ArrayList<ListEntry>();

		for (String s : DYNServerMod.roster) {
			rlist.add(new StringEntry(s));
		}

		rosterDisplayList = new ScrollableDisplayList((int) (width * .475), (int) (height * .25), (int) (width / 2.75),
				150, 15, rlist);
		rosterDisplayList.setId("roster");
		registerComponent(rosterDisplayList);

		numberOfStudentsOnRoster = new TextLabel((int) (width * .5) + 20, (int) (height * .2), 90, 20, Color.black,
				"Roster Count: " + DYNServerMod.roster.size(), TextAlignment.LEFT);
		registerComponent(numberOfStudentsOnRoster);

		// the side buttons
		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_1.getFirst()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_1.getSecond()), 30, 30,
				DYNServerConstants.STUDENTS_IMAGE).setIsEnabled(true).addHoverText("Manage Classroom")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new Home())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_2.getFirst()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_2.getSecond()), 30, 30,
				DYNServerConstants.ROSTER_IMAGE).setIsEnabled(false).addHoverText("Student Rosters")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new Roster())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_3.getFirst()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_3.getSecond()), 30, 30,
				DYNServerConstants.STUDENT_IMAGE).setIsEnabled(true).addHoverText("Manage a Student")
						.doesDrawHoverText(true).setClickListener(but -> getStage().display(new ManageStudent())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_4.getFirst()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_4.getSecond()), 30, 30,
				DYNServerConstants.INVENTORY_IMAGE).setIsEnabled(true).addHoverText("Manage Inventory")
						.doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new ManageStudentsInventory())));

		registerComponent(new PictureButton((int) (width * DYNServerConstants.BUTTON_LOCATION_5.getFirst()),
				(int) (height * DYNServerConstants.BUTTON_LOCATION_5.getSecond()), 30, 30,
				DYNServerConstants.ACHIEVEMENT_IMAGE).setIsEnabled(true).addHoverText("Award Achievements")
						.doesDrawHoverText(true)
						.setClickListener(but -> getStage().display(new MonitorAchievements())));

		// The background
		registerComponent(new Picture(width / 8, (int) (height * .15), (int) (width * (6.0 / 8.0)), (int) (height * .8),
				DYNServerConstants.BG1_IMAGE));
	}

	// public void refreshDropdown(String id, List<Pair<String, Integer>>
	// content) {
	// if (id.equals("org")) {
	// for (IGui component : getComponentsList()) {
	// if (component.getId() != null && component.getId().equals("org")) {
	// for (Pair<String, Integer> item : content) {
	// System.out.println("adding: " + item.getFirst() + ", " +
	// item.getSecond());
	// ((DropDown<Integer>)component).add(item.getFirst(), item.getSecond());
	// orgs.setIsEnabled(true);
	// }
	// }
	//
	// }
	// } else if (id.equals("program")) {
	// for (IGui component : getComponentsList()) {
	// if (component.getId() != null && component.getId().equals("program")) {
	// for (Pair<String, Integer> item : content) {
	// System.out.println("adding: " + item.getFirst() + ", " +
	// item.getSecond());
	// ((DropDown<Integer>)component).add(item.getFirst(), item.getSecond());
	// }
	// }
	// }
	// } else if (id.equals("schedule")) {
	// for (IGui component : getComponentsList()) {
	// if (component.getId() != null && component.getId().equals("schedule")) {
	// for (Pair<String, Integer> item : content) {
	// System.out.println("adding: " + item.getFirst() + ", " +
	// item.getSecond());
	// ((DropDown<Integer>)component).add(item.getFirst(), item.getSecond());
	// }
	// }
	// }
	// }
	// }
}
