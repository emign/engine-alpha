/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ea.internal.ani;

import ea.*;

/**
 * Beschreiben Sie hier die Klasse KreisAnimierer. Er animiert vorlaeufig nur im Urzeigersinn.
 *
 * @author Michael Andonie
 */
@SuppressWarnings ("serial")
public class KreisAnimierer extends Animierer {
	/**
	 * Der Schritt, der bei einem Aufruf gemacht wird
	 */
	private static final double schritt = Math.PI / 100;

	/**
	 * Der Radius des Kreises, der die Bewegung beschreibt.
	 */
	private final double radius;

	/**
	 * Das Zentrum des die Drehbewegung beschreibenden Kreises.
	 */
	private final Punkt zentrum;

	/**
	 * Der aktuelle Winkel im Gradmass zur Vertikalen.<br /> Im <b>Bogenmass</b>
	 */
	private double winkel;

	/**
	 * Der Endpunkt des letzten Animationsschrittes
	 */
	private Punkt letzter;

	/**
	 * Konstruktor fuer Objekte der Klasse KreisAnimierer
	 *
	 * @param ziel
	 * 		Das zu animierende Objekt
	 * @param intervall
	 * 		Der TickerIntervall; fuer die tick()-Geschwindikeit.
	 * @param loop
	 * 		Ob die Animation dauerhaft wiederholt (geloopt) werden soll.
	 * @param m
	 * 		Der Manager, an dem spaeter animiert werden soll.
	 * @param listener
	 * 		Der AnimationsEndeReagierbar-Listener, der am Ende der Animation aufgerufen wird.
	 */
	public KreisAnimierer (Raum ziel, Punkt zentrum, int intervall, boolean loop, Manager m, AnimationsEndeReagierbar listener) {
		super(ziel, intervall, loop, m, listener);
		this.zentrum = zentrum;
		this.letzter = ziel.zentrum();
		Punkt zielMitte = ziel.zentrum();
		radius = zentrum.abstand(zielMitte);
		winkel = new Gerade(zielMitte, zentrum).winkel();
		if (zentrum.realX() < zielMitte.realX() && zentrum.realY() < zielMitte.realY()) {
			winkel = Math.PI - winkel;
		}
	}

	public void animationsSchritt () {
		winkel += schritt;
		float x, y;
		x = (float) ((-Math.sin(winkel)) * radius) + zentrum.realX();
		y = (float) ((Math.cos(winkel)) * radius) + zentrum.realY();
		Vektor v = new Vektor(x - letzter.realX(), y - letzter.realY());
		ziel.bewegen(v);
		letzter = letzter.verschobenerPunkt(v);
	}
}
