package com.app.gimnasio.data.local

import com.app.gimnasio.data.model.ExerciseInfo
import com.app.gimnasio.data.model.MuscleGroup

/**
 * Ejercicios pre-cargados organizados por grupo muscular.
 * assetImage: nombre del archivo en assets/exercise_images/ (sin extensión) o null.
 */
data class SeedExercise(
    val name: String,
    val description: String,
    val muscleGroup: MuscleGroup,
    val assetImage: String? = null
)

object ExerciseSeedData {

    fun getAll(): List<SeedExercise> = pectorales + hombros + triceps + espalda +
            biceps + trapecio + antebrazos + cuadriceps + isquiotibiales +
            gluteos + abductores + aductores + gemelos + abdominales + lumbares

    // ─── PECTORALES ───────────────────────────────────────────────
    private val pectorales = listOf(
        SeedExercise("Press banca con barra", "Acostado en banco plano, baja la barra al pecho y empuja hacia arriba. Ejercicio principal para pectoral mayor.", MuscleGroup.PECTORALES, "press_banca_barra"),
        SeedExercise("Press banca inclinado", "Press en banco inclinado a 30-45°. Enfatiza la porción superior (clavicular) del pectoral.", MuscleGroup.PECTORALES, "press_banca_inclinado"),
        SeedExercise("Press banca declinado", "Press en banco declinado. Enfatiza la porción inferior del pectoral.", MuscleGroup.PECTORALES, "press_banca_declinado"),
        SeedExercise("Press con mancuernas", "Acostado en banco plano, empuja las mancuernas hacia arriba. Mayor rango de movimiento que con barra.", MuscleGroup.PECTORALES, "press_mancuernas"),
        SeedExercise("Aperturas con mancuernas", "Acostado en banco, abre los brazos con mancuernas en arco y junta arriba. Aísla el pectoral.", MuscleGroup.PECTORALES, "aperturas_mancuernas"),
        SeedExercise("Aperturas en máquina (Pec Deck)", "Sentado en la máquina, junta los brazos al frente. Aislamiento del pectoral.", MuscleGroup.PECTORALES),
        SeedExercise("Cruces en polea alta", "De pie entre poleas altas, cruza los cables hacia abajo al frente. Trabaja la parte inferior del pecho.", MuscleGroup.PECTORALES, "cruces_polea"),
        SeedExercise("Cruces en polea baja", "De pie entre poleas bajas, cruza los cables hacia arriba. Trabaja la parte superior del pecho.", MuscleGroup.PECTORALES),
        SeedExercise("Fondos en paralelas", "Suspendido en barras paralelas, baja el cuerpo flexionando codos e inclina el torso. Trabaja pecho inferior y tríceps.", MuscleGroup.PECTORALES, "fondos_paralelas"),
        SeedExercise("Flexiones (Lagartijas)", "Boca abajo, empuja el cuerpo hacia arriba con las manos. Ejercicio básico de empuje para pecho.", MuscleGroup.PECTORALES, "flexiones"),
        SeedExercise("Press en máquina", "Sentado en máquina de press, empuja hacia adelante. Ideal para principiantes por el movimiento guiado.", MuscleGroup.PECTORALES),
        SeedExercise("Pullover con mancuerna", "Acostado transversal en banco, baja la mancuerna detrás de la cabeza y sube. Trabaja pecho y serrato.", MuscleGroup.PECTORALES, "pullover_mancuerna"),
    )

    // ─── HOMBROS ──────────────────────────────────────────────────
    private val hombros = listOf(
        SeedExercise("Press militar con barra", "De pie o sentado, empuja la barra desde los hombros hacia arriba. Ejercicio compuesto principal para deltoides.", MuscleGroup.HOMBROS, "press_militar"),
        SeedExercise("Press Arnold", "Sentado con mancuernas, inicia con agarre supino y rota mientras empujas arriba. Trabaja las tres cabezas del deltoides.", MuscleGroup.HOMBROS, "press_arnold"),
        SeedExercise("Press con mancuernas sentado", "Sentado en banco con respaldo, empuja las mancuernas hacia arriba desde los hombros.", MuscleGroup.HOMBROS),
        SeedExercise("Elevaciones laterales", "De pie, eleva las mancuernas a los lados hasta la altura de los hombros. Aísla el deltoides lateral.", MuscleGroup.HOMBROS, "elevaciones_laterales"),
        SeedExercise("Elevaciones frontales", "De pie, eleva las mancuernas al frente hasta la altura de los hombros. Trabaja el deltoides anterior.", MuscleGroup.HOMBROS, "elevaciones_frontales"),
        SeedExercise("Pájaros (Elevaciones posteriores)", "Inclinado hacia adelante, eleva las mancuernas a los lados. Trabaja el deltoides posterior.", MuscleGroup.HOMBROS, "pajaros"),
        SeedExercise("Remo al mentón", "De pie, sube la barra pegada al cuerpo hasta el mentón. Trabaja deltoides y trapecio.", MuscleGroup.HOMBROS, "remo_menton"),
        SeedExercise("Face Pull", "Con polea alta y cuerda, tira hacia la cara separando las manos. Trabaja deltoides posterior y rotadores.", MuscleGroup.HOMBROS, "face_pull"),
        SeedExercise("Elevaciones laterales en polea", "De pie al lado de una polea baja, eleva el brazo lateralmente. Tensión constante en el deltoides lateral.", MuscleGroup.HOMBROS),
        SeedExercise("Press en máquina de hombros", "Sentado en máquina, empuja hacia arriba. Movimiento guiado y seguro para deltoides.", MuscleGroup.HOMBROS),
    )

    // ─── TRÍCEPS ──────────────────────────────────────────────────
    private val triceps = listOf(
        SeedExercise("Fondos en banco", "Manos en banco detrás, baja el cuerpo flexionando codos y sube. Aísla el tríceps.", MuscleGroup.TRICEPS, "fondos_banco"),
        SeedExercise("Press francés con barra Z", "Acostado en banco, baja la barra Z hacia la frente flexionando codos y extiende. Trabaja las tres cabezas del tríceps.", MuscleGroup.TRICEPS, "press_frances"),
        SeedExercise("Extensión de tríceps en polea alta", "De pie frente a polea alta, extiende los codos empujando la barra hacia abajo.", MuscleGroup.TRICEPS, "extension_polea"),
        SeedExercise("Extensión con cuerda en polea", "De pie frente a polea alta con cuerda, extiende y separa las manos abajo. Énfasis en cabeza lateral.", MuscleGroup.TRICEPS, "extension_cuerda"),
        SeedExercise("Patada de tríceps", "Inclinado con mancuerna, extiende el brazo hacia atrás. Aísla la cabeza larga del tríceps.", MuscleGroup.TRICEPS, "patada_triceps"),
        SeedExercise("Extensión overhead con mancuerna", "Sentado o de pie, baja la mancuerna detrás de la cabeza y extiende. Estira la cabeza larga del tríceps.", MuscleGroup.TRICEPS, "extension_overhead"),
        SeedExercise("Press cerrado con barra", "Press banca con agarre estrecho. Enfatiza el tríceps sobre el pecho.", MuscleGroup.TRICEPS, "press_cerrado"),
        SeedExercise("Flexiones diamante", "Flexiones con manos juntas formando un diamante. Gran activación del tríceps.", MuscleGroup.TRICEPS, "flexiones_diamante"),
        SeedExercise("Extensión en polea invertida", "De pie frente a polea alta, agarre supino y extiende hacia abajo. Énfasis en cabeza medial.", MuscleGroup.TRICEPS),
    )

    // ─── ESPALDA ──────────────────────────────────────────────────
    private val espalda = listOf(
        SeedExercise("Dominadas (Pull-ups)", "Colgado de barra, sube el cuerpo hasta que el mentón pase la barra. Ejercicio rey para dorsales.", MuscleGroup.ESPALDA, "dominadas"),
        SeedExercise("Dominadas agarre neutro", "Dominadas con palmas enfrentadas. Trabaja dorsales con menor estrés en hombros.", MuscleGroup.ESPALDA),
        SeedExercise("Jalón al pecho (Lat Pulldown)", "Sentado en máquina, tira la barra hacia el pecho. Simula la dominada con peso ajustable.", MuscleGroup.ESPALDA, "jalon_pecho"),
        SeedExercise("Jalón agarre cerrado", "En máquina de jalón, usa agarre cerrado y tira al pecho. Énfasis en la parte baja del dorsal.", MuscleGroup.ESPALDA),
        SeedExercise("Remo con barra", "Inclinado, tira la barra hacia el abdomen. Trabaja dorsales, romboides y trapecio medio.", MuscleGroup.ESPALDA, "remo_barra"),
        SeedExercise("Remo con mancuerna", "Apoyado en banco con una mano, rema la mancuerna hacia la cadera. Trabajo unilateral de espalda.", MuscleGroup.ESPALDA, "remo_mancuerna"),
        SeedExercise("Remo en polea baja", "Sentado, tira el agarre de polea baja hacia el abdomen. Trabaja dorsales y romboides.", MuscleGroup.ESPALDA, "remo_polea"),
        SeedExercise("Remo en máquina T", "Inclinado sobre la máquina T, rema la barra hacia el pecho. Gran ejercicio para espesor de espalda.", MuscleGroup.ESPALDA, "remo_t"),
        SeedExercise("Pullover en polea alta", "De pie frente a polea alta, brazos extendidos, tira la barra hacia los muslos. Aísla dorsales.", MuscleGroup.ESPALDA, "pullover_polea"),
        SeedExercise("Remo Pendlay", "Remo con barra desde el suelo en cada repetición. Desarrolla fuerza explosiva de espalda.", MuscleGroup.ESPALDA),
        SeedExercise("Remo en máquina", "Sentado en máquina de remo, tira las asas hacia el torso. Movimiento guiado para espalda.", MuscleGroup.ESPALDA),
    )

    // ─── BÍCEPS ───────────────────────────────────────────────────
    private val biceps = listOf(
        SeedExercise("Curl con barra recta", "De pie, flexiona los codos subiendo la barra. Ejercicio básico para masa de bíceps.", MuscleGroup.BICEPS, "curl_barra"),
        SeedExercise("Curl con barra Z", "De pie, curl con barra Z. Reduce estrés en muñecas comparado con barra recta.", MuscleGroup.BICEPS),
        SeedExercise("Curl con mancuernas alterno", "De pie, alterna curls con cada brazo. Permite concentración en cada bíceps.", MuscleGroup.BICEPS, "curl_mancuernas"),
        SeedExercise("Curl martillo", "Curl con agarre neutro (palmas enfrentadas). Trabaja bíceps braquial y braquiorradial.", MuscleGroup.BICEPS, "curl_martillo"),
        SeedExercise("Curl en banco inclinado", "Sentado en banco inclinado, curls con mancuernas. Estira más la cabeza larga del bíceps.", MuscleGroup.BICEPS, "curl_inclinado"),
        SeedExercise("Curl concentrado", "Sentado, codo apoyado en la rodilla, curl con mancuerna. Máximo aislamiento del bíceps.", MuscleGroup.BICEPS, "curl_concentrado"),
        SeedExercise("Curl en banco Scott (Predicador)", "Brazos apoyados en banco Scott, curl con barra o mancuerna. Aísla el bíceps eliminando impulso.", MuscleGroup.BICEPS, "curl_scott"),
        SeedExercise("Curl en polea baja", "De pie frente a polea baja, curl con barra o cuerda. Tensión constante en el bíceps.", MuscleGroup.BICEPS),
        SeedExercise("Curl araña", "Apoyado boca abajo en banco inclinado, curls con mancuernas. Énfasis en la cabeza corta del bíceps.", MuscleGroup.BICEPS),
        SeedExercise("Curl 21s", "7 reps parciales abajo, 7 arriba y 7 completas. Técnica de alta intensidad para bíceps.", MuscleGroup.BICEPS),
    )

    // ─── TRAPECIO ─────────────────────────────────────────────────
    private val trapecio = listOf(
        SeedExercise("Encogimientos con barra", "De pie, sube los hombros hacia las orejas con barra. Ejercicio principal para trapecio superior.", MuscleGroup.TRAPECIO, "encogimientos_barra"),
        SeedExercise("Encogimientos con mancuernas", "De pie, encoge los hombros con mancuernas a los lados. Permite mayor rango de movimiento.", MuscleGroup.TRAPECIO, "encogimientos_mancuernas"),
        SeedExercise("Remo al mentón con barra", "De pie, sube la barra pegada al cuerpo hasta el mentón. Trabaja trapecio superior y deltoides.", MuscleGroup.TRAPECIO, "remo_menton_trap"),
        SeedExercise("Face Pull", "Con polea alta y cuerda, tira hacia la cara. Trabaja trapecio medio e inferior y deltoides posterior.", MuscleGroup.TRAPECIO),
        SeedExercise("Encogimientos en máquina Smith", "Encogimientos usando la barra guiada de la máquina Smith. Movimiento controlado para trapecio.", MuscleGroup.TRAPECIO),
        SeedExercise("Remo con barra agarre ancho", "Remo con barra e inclinación, agarre más ancho que los hombros. Enfatiza trapecio medio y romboides.", MuscleGroup.TRAPECIO),
        SeedExercise("Farmer's Walk", "Camina sosteniendo pesas pesadas a los lados. Trabaja trapecio, antebrazo y core.", MuscleGroup.TRAPECIO),
    )

    // ─── ANTEBRAZOS ───────────────────────────────────────────────
    private val antebrazos = listOf(
        SeedExercise("Curl de muñeca con barra", "Sentado, antebrazos en las rodillas, flexiona muñecas con barra. Trabaja flexores del antebrazo.", MuscleGroup.ANTEBRAZOS),
        SeedExercise("Curl de muñeca inverso", "Sentado, antebrazos en rodillas con agarre prono, extiende muñecas. Trabaja extensores del antebrazo.", MuscleGroup.ANTEBRAZOS),
        SeedExercise("Curl de muñeca tras espalda", "De pie, barra detrás del cuerpo, flexiona muñecas. Trabaja flexores con mayor rango.", MuscleGroup.ANTEBRAZOS),
        SeedExercise("Farmer's Walk", "Camina sosteniendo mancuernas pesadas. Excelente para fuerza de agarre y antebrazos.", MuscleGroup.ANTEBRAZOS),
        SeedExercise("Pinch Grip (Agarre de pinza)", "Sostén discos con los dedos el mayor tiempo posible. Desarrolla fuerza de agarre.", MuscleGroup.ANTEBRAZOS),
        SeedExercise("Roller de muñeca", "Con rodillo de muñeca, enrolla y desenrolla la cuerda con peso. Trabaja flexores y extensores.", MuscleGroup.ANTEBRAZOS),
        SeedExercise("Curl martillo", "Curl con agarre neutro. Además del bíceps, trabaja intensamente el braquiorradial del antebrazo.", MuscleGroup.ANTEBRAZOS),
    )

    // ─── CUÁDRICEPS ───────────────────────────────────────────────
    private val cuadriceps = listOf(
        SeedExercise("Sentadilla con barra", "Barra en la espalda, baja flexionando rodillas y caderas. Rey de los ejercicios de pierna.", MuscleGroup.CUADRICEPS, "sentadilla_barra"),
        SeedExercise("Sentadilla frontal", "Barra al frente sobre los deltoides, sentadilla. Mayor énfasis en cuádriceps y core.", MuscleGroup.CUADRICEPS, "sentadilla_frontal"),
        SeedExercise("Sentadilla búlgara", "Pie trasero elevado en banco, baja en zancada. Trabajo unilateral intenso de cuádriceps y glúteos.", MuscleGroup.CUADRICEPS),
        SeedExercise("Prensa de piernas", "En máquina de prensa, empuja la plataforma con los pies. Trabaja cuádriceps con menos estrés lumbar.", MuscleGroup.CUADRICEPS, "prensa_piernas"),
        SeedExercise("Hack Squat", "En máquina hack, sentadilla con espalda apoyada. Aísla cuádriceps.", MuscleGroup.CUADRICEPS, "hack_squat"),
        SeedExercise("Extensión de piernas", "Sentado en máquina, extiende las piernas. Aislamiento puro de cuádriceps.", MuscleGroup.CUADRICEPS, "extension_piernas"),
        SeedExercise("Zancadas (Lunges)", "Da un paso al frente y baja. Trabaja cuádriceps, glúteos e isquiotibiales.", MuscleGroup.CUADRICEPS, "zancadas"),
        SeedExercise("Zancadas caminando", "Zancadas con desplazamiento hacia adelante. Trabaja piernas completas y equilibrio.", MuscleGroup.CUADRICEPS),
        SeedExercise("Sentadilla goblet", "Sentadilla sosteniendo una mancuerna o kettlebell al pecho. Ideal para aprender la técnica.", MuscleGroup.CUADRICEPS, "sentadilla_goblet"),
        SeedExercise("Step-ups", "Sube a un cajón o banco con una pierna. Trabajo funcional de cuádriceps y glúteos.", MuscleGroup.CUADRICEPS, "step_ups"),
        SeedExercise("Sentadilla en máquina Smith", "Sentadilla con barra guiada. Mayor estabilidad, ideal para principiantes.", MuscleGroup.CUADRICEPS),
        SeedExercise("Sissy Squat", "Sentadilla inclinándose hacia atrás con rodillas al frente. Aislamiento extremo de cuádriceps.", MuscleGroup.CUADRICEPS),
    )

    // ─── ISQUIOTIBIALES ───────────────────────────────────────────
    private val isquiotibiales = listOf(
        SeedExercise("Peso muerto rumano", "De pie con barra, baja inclinando la cadera con piernas casi rectas. Principal ejercicio para isquiotibiales.", MuscleGroup.ISQUIOTIBIALES, "peso_muerto_rumano"),
        SeedExercise("Peso muerto convencional", "Levanta la barra del suelo con piernas y espalda. Trabaja toda la cadena posterior.", MuscleGroup.ISQUIOTIBIALES, "peso_muerto"),
        SeedExercise("Curl de piernas acostado", "Boca abajo en máquina, flexiona las piernas. Aislamiento de isquiotibiales.", MuscleGroup.ISQUIOTIBIALES, "curl_piernas_acostado"),
        SeedExercise("Curl de piernas sentado", "Sentado en máquina, flexiona las piernas bajo la almohadilla. Aísla isquiotibiales.", MuscleGroup.ISQUIOTIBIALES, "curl_piernas_sentado"),
        SeedExercise("Buenos días (Good Morning)", "Barra en espalda, inclina el torso hacia adelante con piernas casi rectas. Trabaja isquiotibiales y espalda baja.", MuscleGroup.ISQUIOTIBIALES, "buenos_dias"),
        SeedExercise("Peso muerto sumo", "Peso muerto con piernas abiertas y agarre estrecho. Trabaja isquiotibiales, glúteos y aductores.", MuscleGroup.ISQUIOTIBIALES),
        SeedExercise("Nordic curl", "De rodillas, baja el cuerpo lentamente hacia el suelo. Ejercicio excéntrico intenso para isquiotibiales.", MuscleGroup.ISQUIOTIBIALES),
        SeedExercise("Curl con pelota suiza", "Acostado, talones sobre pelota suiza, flexiona piernas rodando la pelota. Trabaja isquiotibiales y core.", MuscleGroup.ISQUIOTIBIALES),
        SeedExercise("Peso muerto con mancuernas", "Peso muerto rumano usando mancuernas. Mayor rango y trabajo de estabilización.", MuscleGroup.ISQUIOTIBIALES),
    )

    // ─── GLÚTEOS ──────────────────────────────────────────────────
    private val gluteos = listOf(
        SeedExercise("Hip Thrust con barra", "Espalda apoyada en banco, empuja caderas hacia arriba con barra. Ejercicio principal para glúteo mayor.", MuscleGroup.GLUTEOS, "hip_thrust"),
        SeedExercise("Puente de glúteos", "Acostado boca arriba, eleva las caderas. Activación directa del glúteo mayor.", MuscleGroup.GLUTEOS, "puente_gluteos"),
        SeedExercise("Sentadilla sumo", "Sentadilla con piernas muy abiertas y pies hacia afuera. Énfasis en glúteos y aductores.", MuscleGroup.GLUTEOS, "sentadilla_sumo"),
        SeedExercise("Peso muerto rumano a una pierna", "Peso muerto con una sola pierna. Trabaja glúteos, isquiotibiales y equilibrio.", MuscleGroup.GLUTEOS),
        SeedExercise("Patada de glúteo en polea", "En polea baja, extiende la pierna hacia atrás. Aislamiento del glúteo mayor.", MuscleGroup.GLUTEOS),
        SeedExercise("Patada de glúteo en máquina", "En máquina específica, empuja la plataforma hacia atrás. Aísla el glúteo.", MuscleGroup.GLUTEOS),
        SeedExercise("Sentadilla búlgara", "Zancada con pie trasero elevado. Gran trabajo de glúteos y cuádriceps.", MuscleGroup.GLUTEOS),
        SeedExercise("Abducción de cadera en máquina", "Sentado, abre las piernas contra la resistencia. Trabaja glúteo medio.", MuscleGroup.GLUTEOS),
        SeedExercise("Step-ups con mancuernas", "Sube a un cajón con peso. Trabaja glúteos de forma funcional.", MuscleGroup.GLUTEOS),
        SeedExercise("Clamshell", "De costado, rodillas flexionadas, abre la rodilla superior. Activa el glúteo medio.", MuscleGroup.GLUTEOS),
    )

    // ─── ABDUCTORES ───────────────────────────────────────────────
    private val abductores = listOf(
        SeedExercise("Abducción en máquina", "Sentado en máquina, abre las piernas contra la resistencia. Trabaja glúteo medio y abductores.", MuscleGroup.ABDUCTORES),
        SeedExercise("Abducción con banda elástica", "De pie o sentado, abre las piernas contra la banda. Activa los abductores.", MuscleGroup.ABDUCTORES),
        SeedExercise("Caminata lateral con banda", "Con banda en las rodillas o tobillos, camina lateralmente. Trabaja abductores y glúteo medio.", MuscleGroup.ABDUCTORES),
        SeedExercise("Elevación lateral de pierna", "De costado, eleva la pierna superior. Aislamiento de abductores.", MuscleGroup.ABDUCTORES),
        SeedExercise("Abducción en polea baja", "De pie al lado de la polea, aleja la pierna del cuerpo. Trabajo controlado de abductores.", MuscleGroup.ABDUCTORES),
        SeedExercise("Sentadilla con banda", "Sentadilla con banda elástica en las rodillas empujando hacia afuera. Activa abductores.", MuscleGroup.ABDUCTORES),
        SeedExercise("Fire Hydrant", "En cuatro puntos, abre la rodilla hacia el costado. Trabaja abductores y glúteo medio.", MuscleGroup.ABDUCTORES),
    )

    // ─── ADUCTORES ────────────────────────────────────────────────
    private val aductores = listOf(
        SeedExercise("Aducción en máquina", "Sentado en máquina, cierra las piernas contra la resistencia. Aísla los aductores.", MuscleGroup.ADUCTORES),
        SeedExercise("Aducción en polea baja", "De pie, cruza la pierna frente al cuerpo con la polea. Trabajo controlado de aductores.", MuscleGroup.ADUCTORES),
        SeedExercise("Sentadilla sumo", "Sentadilla con piernas muy abiertas. Trabaja aductores, glúteos y cuádriceps.", MuscleGroup.ADUCTORES),
        SeedExercise("Sentadilla cosaca", "Zancada lateral profunda alternando piernas. Estira y fortalece aductores.", MuscleGroup.ADUCTORES),
        SeedExercise("Aducción con pelota", "Sentado o acostado, aprieta una pelota entre las rodillas. Activación isométrica de aductores.", MuscleGroup.ADUCTORES),
        SeedExercise("Peso muerto sumo", "Peso muerto con piernas muy abiertas. Trabaja aductores e isquiotibiales.", MuscleGroup.ADUCTORES),
        SeedExercise("Zancada lateral", "Da un paso amplio al costado y baja. Trabaja aductores, cuádriceps y glúteos.", MuscleGroup.ADUCTORES),
    )

    // ─── GEMELOS ──────────────────────────────────────────────────
    private val gemelos = listOf(
        SeedExercise("Elevación de gemelos de pie", "De pie, sube en puntas de pie. Trabaja el gastrocnemio (gemelos).", MuscleGroup.GEMELOS, "gemelos_pie"),
        SeedExercise("Elevación de gemelos sentado", "Sentado en máquina, sube en puntas. Enfatiza el sóleo bajo los gemelos.", MuscleGroup.GEMELOS, "gemelos_sentado"),
        SeedExercise("Gemelos en prensa", "En la prensa de piernas, empuja con las puntas de los pies. Trabaja gemelos con carga.", MuscleGroup.GEMELOS, "gemelos_prensa"),
        SeedExercise("Elevación de gemelos en máquina Smith", "De pie en máquina Smith, sube en puntas. Permite cargar peso de forma segura.", MuscleGroup.GEMELOS),
        SeedExercise("Elevación a una pierna", "Sube en puntas con una sola pierna sosteniendo mancuerna. Trabajo unilateral intenso.", MuscleGroup.GEMELOS),
        SeedExercise("Gemelos en máquina donkey", "Inclinado hacia adelante en máquina, sube en puntas. Estiramiento completo del gastrocnemio.", MuscleGroup.GEMELOS),
        SeedExercise("Saltos a la cuerda", "Salta la cuerda de forma continua. Trabaja gemelos, coordinación y cardio.", MuscleGroup.GEMELOS),
    )

    // ─── ABDOMINALES ──────────────────────────────────────────────
    private val abdominales = listOf(
        SeedExercise("Crunch abdominal", "Acostado, eleva los hombros del suelo contrayendo el abdomen. Ejercicio básico para recto abdominal.", MuscleGroup.ABDOMINALES, "crunch"),
        SeedExercise("Crunch en máquina", "Sentado en máquina de abdominales, flexiona el torso contra la resistencia.", MuscleGroup.ABDOMINALES),
        SeedExercise("Plancha (Plank)", "Apoyado en antebrazos y puntas de pies, mantén el cuerpo recto. Trabaja todo el core isométricamente.", MuscleGroup.ABDOMINALES, "plancha"),
        SeedExercise("Plancha lateral", "De costado, apoyado en un antebrazo, mantén el cuerpo recto. Trabaja oblicuos y core lateral.", MuscleGroup.ABDOMINALES),
        SeedExercise("Elevación de piernas colgado", "Colgado de barra, eleva las piernas rectas. Trabaja abdomen inferior con alta intensidad.", MuscleGroup.ABDOMINALES, "elevacion_piernas"),
        SeedExercise("Elevación de piernas en banco", "Acostado en banco, eleva las piernas. Trabaja la porción inferior del recto abdominal.", MuscleGroup.ABDOMINALES),
        SeedExercise("Russian twist", "Sentado con torso inclinado, gira de lado a lado con peso. Trabaja oblicuos.", MuscleGroup.ABDOMINALES, "russian_twist"),
        SeedExercise("Bicicleta abdominal", "Acostado, alterna codo-rodilla opuestos pedaleando. Trabaja recto abdominal y oblicuos.", MuscleGroup.ABDOMINALES, "bicicleta_abdominal"),
        SeedExercise("Ab Wheel Rollout", "De rodillas, rueda la rueda abdominal hacia adelante y regresa. Trabajo intenso de todo el core.", MuscleGroup.ABDOMINALES, "ab_wheel"),
        SeedExercise("Mountain Climbers", "En posición de plancha, alterna rodillas al pecho rápidamente. Cardio y abdominales.", MuscleGroup.ABDOMINALES),
        SeedExercise("Cable Crunch", "De rodillas frente a polea alta, flexiona el torso hacia abajo. Abdominales con resistencia progresiva.", MuscleGroup.ABDOMINALES),
        SeedExercise("Dead Bug", "Acostado boca arriba, extiende brazo y pierna opuestos alternando. Trabaja core profundo y estabilidad.", MuscleGroup.ABDOMINALES),
    )

    // ─── LUMBARES ─────────────────────────────────────────────────
    private val lumbares = listOf(
        SeedExercise("Hiperextensiones", "En banco de hiperextensiones, baja el torso y sube. Ejercicio principal para erectores espinales.", MuscleGroup.LUMBARES, "hiperextensiones"),
        SeedExercise("Buenos días (Good Morning)", "Barra en espalda, inclina el torso hacia adelante. Trabaja espalda baja e isquiotibiales.", MuscleGroup.LUMBARES),
        SeedExercise("Superman", "Boca abajo, eleva brazos y piernas simultáneamente. Fortalece toda la espalda baja.", MuscleGroup.LUMBARES, "superman"),
        SeedExercise("Peso muerto", "Levanta la barra del suelo. Trabaja intensamente los erectores espinales y toda la cadena posterior.", MuscleGroup.LUMBARES),
        SeedExercise("Bird Dog", "En cuatro puntos, extiende brazo y pierna opuestos. Trabaja lumbares y estabilidad del core.", MuscleGroup.LUMBARES),
        SeedExercise("Puente de glúteos", "Acostado boca arriba, eleva caderas. Fortalece glúteos y zona lumbar.", MuscleGroup.LUMBARES),
        SeedExercise("Extensión lumbar en máquina", "Sentado en máquina de extensión lumbar, empuja hacia atrás. Aislamiento controlado.", MuscleGroup.LUMBARES),
        SeedExercise("Reverse Hyper", "Boca abajo en banco, eleva las piernas hacia atrás. Descomprime la columna mientras fortalece.", MuscleGroup.LUMBARES),
    )
}
