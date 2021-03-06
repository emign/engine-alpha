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

package ea;

import ea.internal.collision.Collider;
import ea.internal.collision.ColliderGroup;
import ea.internal.gra.Listung;

import java.awt.*;

/**
 * Ein Objekt, das aus n primitiven Geometrischen Formen - <b>Dreiecken</b> - besteht
 *
 * @author Michael Andonie
 */
public abstract class Geometrie extends Raum implements Leuchtend, Listung {
	/**
	 * Die Dimension des Objektes; zur schnellen Ausgabe
	 */
	protected BoundingRechteck dimension;

	/**
	 * Die einzelnen, grafisch darstellbaren Formen, aus denen dieses Geometrie-Objekt besteht.
	 */
	private Dreieck[] formen;

	/**
	 * Gibt an, ob dieses Geometrie-Objekt gerade leuchtet
	 */
	private boolean leuchtet = false;

	/**
	 * Der Leuchtzaehler fuer die Leucht-Animationen
	 */
	private int leuchtzaehler = 0;

	/**
	 * Die Farbe, die sich das Objekt merkt, wenn es zu leuchten anfaengt, um wieder die alte
	 * herstellen zu koennen.
	 */
	private Color alte = Color.white;

	/**
	 * Konstruktor.
	 *
	 * @param x
	 * 		bestimmende x-Koordinate
	 * @param y
	 * 		bestimmende y-Koordinate
	 */
	public Geometrie (float x, float y) {
		position = new Punkt(x, y);
		dimension = new BoundingRechteck(x, y, 0, 0);
		super.leuchterAnmelden(this);
	}

	/**
	 * Zeichnet das Objekt.<br /> heisst in diesem Fall das saemtliche Unterdreiecke gezeichnet
	 * werden.
	 */
	public void zeichnen (Graphics2D g, BoundingRechteck r) {
		super.beforeRender(g, r);

		for (int i = 0; i < formen.length; i++) {
			formen[i].zeichnen(g, r);
		}

		super.afterRender(g, r);
	}

	public BoundingRechteck dimension () {
		return dimension.klon();
	}

	/**
	 * Verschiebt das Objekt.
	 *
	 * @param v
	 * 		Der Vektor, der die Verschiebung des Objekts angibt.
	 *
	 * @see Vektor
	 */
	@Override
	public void verschieben (Vektor v) {
		super.verschieben(v);
		for (int i = 0; i < formen.length; i++) {
			formen[i].verschieben(v);
		}
		dimension = dimension.verschobeneInstanz(v);
	}

	/**
	 * {@inheritDoc} Collider ist eine Gruppierung aus den Collidern der Dreiecke, die dieses Objekt
	 * ausmachen.
	 */
	@Override
	public Collider erzeugeCollider () {
		ColliderGroup group = new ColliderGroup();
		for (Dreieck d : formen) {
			group.addCollider(d.erzeugeCollider());
		}
		return group;
	}

	/**
	 * Berechnet exakter alle Rechteckigen Flaechen, auf denen dieses Objekt liegt.<br /> Diese
	 * Methode wird von komplexeren Gebilden, wie geometrischen oder Listen ueberschrieben.
	 *
	 * @return Alle Rechtecksflaechen, auf denen dieses Objekt liegt. Ist standartisiert ein Array
	 * der Groesse 1 mit der <code>dimension()</code> als Inhalt.
	 */
	@Override
	public BoundingRechteck[] flaechen () {
		BoundingRechteck[] ret = new BoundingRechteck[formen.length];
		for (int i = 0; i < formen.length; i++) {
			ret[i] = formen[i].dimension();
		}
		return ret;
	}

	/**
	 * Diese Methode loescht alle eventuell vorhandenen Referenzen innerhalb der Engine auf dieses
	 * Objekt, damit es problemlos geloescht werden kann.<br /> <b>Achtung:</b> zwar werden
	 * hierdurch alle Referenzen geloescht, die <b>nur innerhalb</b> der Engine liegen (dies
	 * betrifft vor allem Animationen etc), jedoch nicht die innerhalb eines
	 * <code>Knoten</code>-Objektes!!!!!!!!!<br /> Das heisst, wenn das Objekt an einem Knoten liegt
	 * (was <b>immer der Fall ist, wenn es auch gezeichnet wird (siehe die Wurzel des
	 * Fensters)</b>), muss es trotzdem selbst geloescht werden, <b>dies erledigt diese Methode
	 * nicht!!</b>.<br /> Diese Klasse ueberschreibt die Methode wegen des Leuchtens.
	 */
	@Override
	public void loeschen () {
		super.leuchterAbmelden(this);
		super.loeschen();
	}

	/**
	 * Setzt ganzheitlich die Farbe der gesamten geometrischen Figur auf eine Farbe.
	 *
	 * @param f
	 * 		Die Farbe, die das Objekt haben soll.
	 *
	 * @see Farbe
	 */
	public void farbeSetzen (Farbe f) {
		farbeSetzen(f.wert());
	}

	/**
	 * Setzt ganzheitlich die Farbe aller Formen auf eine bestimmte Farbe.<br /> Dadurch faerbt sich
	 * im Endeffekt das ganze Objekt neu ein.
	 *
	 * @param c
	 * 		Die neue Farbe
	 */
	public void farbeSetzen (Color c) {
		alte = c;
		if (formen == null) {
			formen = neuBerechnen();
		}
		for (int i = 0; i < formen.length; i++) {
			formen[i].setColor(c);
		}
	}

	/**
	 * In dieser Methode werden saemtliche Dreiecke neu berechnet und die Referenz bei Aufruf in der
	 * Superklasse hierauf gesetzt
	 *
	 * @return Ein Dreieck-Array mit allen, die Figur beschreibenden Dreiecken als Inhalt.
	 */
	public abstract Dreieck[] neuBerechnen ();

	/**
	 * Setzt, ob dieses Geometrie-Objekt leuchten soll.<br /> Ist dies der Fall, so werden immer
	 * wieder schnell dessen Farben geaendert; so entsteht ein Leuchteffekt.
	 *
	 * @param leuchtet
	 * 		Ob dieses Objekt nun leuchten soll oder nicht (mehr).
	 */
	@Override
	public void leuchtetSetzen (boolean leuchtet) {
		if (this.leuchtet == leuchtet) {
			return;
		}
		this.leuchtet = leuchtet;
		if (leuchtet) {
			alte = formen[0].getColor();
		} else {
			this.farbeSetzen(alte);
		}
	}

	/**
	 * Fuehrt einen Leuchtschritt aus.<br /> Dies heisst, dass in dieser Methode die Farbe einfach
	 * gewechselt wird. Da diese Methode schnell und oft hintereinander ausgefuehrt wird, soll so
	 * der Leuchteffekt entstehen.<br /> <b>Diese Methode sollte nur innerhalb der Engine
	 * ausgefuehrt werden! Also nicht fuer den Entwickler gedacht.</b>
	 */
	@Override
	public void leuchtSchritt () {
		for (int i = 0; i < formen.length; i++) {
			leuchtzaehler++;
			leuchtzaehler %= farbzyklus.length;
			formen[i].setColor(farbzyklus[leuchtzaehler]);
		}
	}

	/**
	 * Gibt wieder, ob das Leuchtet-Objekt gerade leuchtet oder nicht.
	 *
	 * @return <code>true</code>, wenn das Objekt gerade leuchtet, wenn nicht, dann ist die
	 * Rueckgabe <code>false</code>
	 */
	@Override
	public boolean leuchtet () {
		return this.leuchtet;
	}

	/**
	 * aktualisisert die Dreiecke, aus denen die Figur besteht UND weisst sie ein. Diese Methode
	 * MUSS am Ende eines jeden Konstruktors einer Klasse stehen, die sich hieraus ableitet<br />
	 * Zugrunde liegt eine neue Wertzuweisung des Arrays, es wird <code>neuBerechnen()</code>
	 * aufgerufen.
	 */
	protected void aktualisierenFirst () {
		aktualisieren();
		farbeSetzen("Weiss");
	}

	/**
	 * aktualisisert die Dreiecke, aus denen die Figur besteht.<br /> Zugrunde liegt eine neue
	 * Wertzuweisung des Arrays, es wird <code>neuBerechnen()</code> aufgerufen.
	 */
	protected void aktualisieren () {
		formen = neuBerechnen();

		for (int i = 0; i < formen.length; i++) {
			formen[i].setColor(alte);
		}

		dimension = ausDreiecken(formen);
	}

	/**
	 * Setzt ganzheitlich die Farbe aller Formen auf eine bestimmte Farbe.<br /> Dadurch faerbt sich
	 * im Endeffekt das ganze Objekt neu ein.
	 *
	 * @param farbe
	 * 		Der String-Wert der Farbe. Zu der Zuordnung siehe <b>Handbuch</b>
	 */
	public void farbeSetzen (String farbe) {
		farbeSetzen(zuFarbeKonvertieren(farbe));
	}

	/**
	 * Berechnet ein neues BoundingRechteck fuer ein Array aus Dreiecken
	 */
	public static BoundingRechteck ausDreiecken (Dreieck[] ecke) {
		BoundingRechteck r = ecke[0].dimension();
		for (int i = 1; i < ecke.length; i++) {
			r = r.summe(ecke[i].dimension());
		}
		return r;
	}

	/**
	 * Gibt alle Unterdreiecke dieser Geometrie-Figur wieder.<br />
	 *
	 * @return Ein Array mit allen Dreiecken dieser Figur.
	 */
	public Dreieck[] formen () {
		return this.formen;
	}
}
