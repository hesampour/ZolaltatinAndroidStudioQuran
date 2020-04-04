package ir.yasansoft.AndroidStudioQuran;

public class pageInformation {

	public int pageNum;
	public int suraNum;
	public int verseNum;
	
	public String getSuraName()
	{
		String arraySura[] = { "الحمد", "البقرة", "آل عمران", "النساء",
				"المائدة", "الأنعام", "الأعراف", "الأنفال", "التوبة", "يونس",
				"هود", "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل", "الإسراء",
				"الكهف", "مريم", "طه", "الأنبياء", "الحج", "المؤمنون", "النور",
				"الفرقان", "الشعراء", "النمل", "القصص", "العنكبوت", "الروم",
				"لقمان", "السجدة", "الأحزاب", "سبأ", "فاطر", "يس", "الصافات",
				"ص", "الزمر", "غافر", "فصلت", "الشورى", "الزخرف", "الدخان",
				"الجاثية", "الأحقاف", "محمد", "الفتح", "الحجرات", "ق",
				"الذاريات", "الطور", "النجم", "القمر", "الرحمن", "الواقعة",
				"الحديد", "المجادلة", "الحشر", "الممتحنة", "الصف", "الجمعة",
				"المنافقون", "التغبن", "الطلاق", "التحريم", "الملك", "القلم",
				"الحاقة", "المعارج", "نوح", "الجن", "المزمل", "المدثر",
				"القيامة", "الإنسان", "المرسلات", "النبأ", "النازعات", "عبس ",
				"التكوير", "الإنفطار", "المطففين", "الانشقاق", "البروج",
				"الطارق", "الأعلى", "الغاشية", "الفجر", "البلد", "الشمس",
				"الليل", "الضحى", "الانشراح", "التين", "العلق", "القدر",
				"البينة", "الزلزال", "العاديات", "القارعة", "التكاثر", "العصر",
				"الهمزة", "الفيل", "قريش", "الماعون", "الكوثر", "الكافرون",
				"النصر", "لهب", "الإخلاص", "الفلق", "الناس "

		};
		if(suraNum > 0)
			return arraySura[suraNum-1];
		else
			return arraySura[0];
		
	}
}