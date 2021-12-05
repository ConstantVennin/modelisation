package utils;

/**
 * Enumération de chaque transformation possible
 */
public enum TransformationType {
	XTRANSLATION(0), YTRANSLATION(1), ZTRANSLATION(2), XROTATION(3), YROTATION(4), ZROTATION(5), SCALE(6),
	CHANGESIDE(-1),CHANGERENDERMODE(-1),CHANGECOLOR(-1),CHANGECANVASSCALEFACTOR(-1),CHANGECANVASSIZE(-1),LOADMODEL(-1),CHANGELIGHTPARAMETER(-1);
	
	private int repereIndex;
	
	private TransformationType(int repereIndex) {
		this.repereIndex=repereIndex;
	}
	
	public int getRepereIndex() {
		return repereIndex;
	}
}
