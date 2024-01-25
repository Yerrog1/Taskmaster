package es.iesteis.proyectoud4bugstars.entity;

public enum TaskStatus {
	/**
	 * Creada pro sin confirmar.
	 */
	REPORTADA,
	/**
	 * Aprobada.
	 */
	POR_HACER,
	/**
	 * Cuando ya tiene a alguien asignado.
	 */
	EN_PROCESO,
	/**
	 * Terminada pero falta revisarla y confirmar que funciona bien.
	 */
	EN_REVISION,
	/**
	 * Acabada.
	 */
	TERMINADA,
	/**
	 * Ya no se va a hacer
	 */
	CANCELADA;
}
