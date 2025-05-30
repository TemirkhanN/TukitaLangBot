-- liquibase formatted sql

-- changeset liquibase:1748547963224-1 splitStatements:false
CREATE TABLE "dictionary" ("id" INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 1176) NOT NULL, "word" VARCHAR(255), "translation" VARCHAR(255), "part_of_speech" VARCHAR(100), "description" TEXT, "context" TEXT, CONSTRAINT "dictionary_pkey" PRIMARY KEY ("id"));

-- changeset liquibase:1748547963224-2 splitStatements:false
CREATE TABLE "learned_resources" ("id" INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 64) NOT NULL, "resource_id" INTEGER, "group_id" VARCHAR(100), "learned_at" TIMESTAMP WITHOUT TIME ZONE, "resource_type" VARCHAR(10) DEFAULT 'word' NOT NULL, CONSTRAINT "learned_words_pkey" PRIMARY KEY ("id"));

-- changeset liquibase:1748547963224-3 splitStatements:false
CREATE TABLE "questions" ("id" INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 2351) NOT NULL, "text" VARCHAR(255), "answer" VARCHAR(255), "variants" TEXT, CONSTRAINT "questions_pkey" PRIMARY KEY ("id"));

-- changeset liquibase:1748547963224-4 splitStatements:false
CREATE TABLE "ch_questions" ("id" UUID NOT NULL, "question_id" INTEGER, "channel_id" VARCHAR(100), "created_at" TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(), CONSTRAINT "ch_questions_pkey" PRIMARY KEY ("id"));

-- changeset liquibase:1748547963224-5 splitStatements:false
CREATE TABLE "ch_question_replies" ("id" INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 109) NOT NULL, "user_id" VARCHAR(32), "channel_id" VARCHAR(100), "question_id" UUID, "is_correct" BOOLEAN, CONSTRAINT "ch_question_replies_pkey" PRIMARY KEY ("id"));

-- changeset liquibase:1748547963224-6 splitStatements:false
CREATE TABLE "channels" ("id" VARCHAR(32) NOT NULL, "added_at" TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(), "is_public" BOOLEAN, "is_active" BOOLEAN DEFAULT TRUE, CONSTRAINT "channels_pkey" PRIMARY KEY ("id"));

-- changeset liquibase:1748547963224-7 splitStatements:false
CREATE TABLE "tasks" ("id" INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 15) NOT NULL, "channel_id" VARCHAR(32), "task_name" VARCHAR(50), "is_active" BOOLEAN DEFAULT TRUE, "frequency" INTEGER, "last_executed_at" TIMESTAMP WITHOUT TIME ZONE, "next_execution_at" TIMESTAMP WITHOUT TIME ZONE, CONSTRAINT "tasks_pkey" PRIMARY KEY ("id"));

-- changeset liquibase:1748547963224-8 splitStatements:false
CREATE TABLE "facts" ("id" INTEGER GENERATED BY DEFAULT AS IDENTITY (START WITH 21) NOT NULL, "text" TEXT, CONSTRAINT "facts_pkey" PRIMARY KEY ("id"));
INSERT INTO facts(text) VALUES
('Несмотря на все изобилие букв в алфавите, не все слова тукитинского языка возможно записать. Так, например, слово "лист" произносится "аль-и". Слово кипеть - "угьугьеду", звучит с носовым звуком, который отличается от звука "гь"'),
('Тукитинский язык является племенным/трибальным языком. По этой причине глаголы в нем часто созвучны с действием, которое они обозначают.
Например "хъухъабеду"(пилить), гlанеду(кричать,орать), мушшаледу(тушить), кьаледу(доить).'),
('Несмотря на идентичность произношений, тукитинский алфавит отличается от аварского. В аварском нет букв "кк" или "чч", хотя звуки этих букв имеются и они одинаковы в обоих языках.
Также, по какой-то причине звук "лълъ" из аварского языка в тукитинском записан, как буква лl. Поскольку на тукитинском, в отличии от аварского, почти нет литературы и прозы, тукитинцам привычнее читать аварскую "лълъ" чем "лl"'),
('Примечательно, в тукитинском языке основной грамматический залог - страдательный. То есть, вместо "я тебе рассказываю" будет "мной тебе рассказывается".'),
('В тукитинском широко используется глагол состояния, как в английском to be(быть).
Например, "я дома" будет звучать, как "я дома есть", а "я Ахмед" будет "Я Ахмед есть".'),
('Поскольку у тукитинского языка нет строгого фундамента, его эволюция проходит незаметно для носителей. Так, например, из речи пропадают и сокращаются ключевые составляющие просто потому что смысл понятен из контекста и без них.

К примеру, в диалоге
 - мини гьанкlу вукlав. (ты где был)
 - къелълъи (дома)

По контексту понятно, что второй участник был дома, поэтому он отбросил местоимение "дини"(я) и глагол "вукlа"(был).

Глагол "эгу" очень часто сокращается до буквы "э".
Мини гьанкlу э(гу)в. Бишти гьанкlу э(гу)бе.

Яркий пример многократного сокращения в обиходе тукитинца выглядит следующим образом "гьоб чукьубда"(эт чё значит), которое раскладывается на слова "гьоб чуб кекьидуб да эгуб"'),
('В тукитинском языке слово "магlарулал" буквально значит "горцы" и в русском именуются аварцами.
Слово "лъарагlал" значит "живущие на равнине" и в русском именуются кумыками.
Слово же тlукидалълъе - тукитинцы, берет начало из села, связанного со словом "тlукя"(горный козёл или тур). То есть, тукитинец - человек родом из места, где водятся горные туры.'),
('Тукита разделено на горное и степное сёла. Горное село является изначальной родиной тукитинцев. Степное село образовалось в результате репрессий в период великой отечественной. Жителей переселили в чечню в дома чеченцев, которые были сосланы в сибирь. После окончания войны владельцы домов  тукитинцы вили вынуждены покинуть и эти дома.
Часть семей вернулась обратно к себе в горы. Часть не смогла поскольку их  дома,судя по всему, были сожжены.
В результате, люди разместились недалеко от камыш-кутана, которое, по-сути, было пустой, болотистой местностью.
На текущий момент оба села полноценные составляющие тукитинской общины с одной культурой и языком.'),
('Род слова в тукитинском выставляется идентично английскому. Мужской род - для мужчин, женский - для женщин, средний - для неодушевленных предметов и животных.

мой муж - див кунтlа;
мой отец - див дада;
мой охотник - див чанахъан;

моя жена - дий гьарукlкlа;
моя мать - дий ила;
моя охотница - дий чанахъан;

мой нож - диб бесун;
моя ложка - диб бегьун;
моё одеяло - диб иргъан;
моя кошка - диб кету.'),
('В тукитинском два местоимения "мы".
"Илълъи - "мы" в классическом смысле идентично русскому, английскому, немецкому или турецкому.
Ищи - "мы", из которого исключен человек, с которым ведётся диалог.

То есть, если вас спросят "вы где?", то использовать "илълъи" будет ошибкой, поскольку "илълъи къелълъи эгу" будет значить "мы с тобой дома". В таких случаях нужно использовать "ищи".');

-- changeset liquibase:1748547963224-9 splitStatements:false
CREATE TABLE "application_version" ("version" VARCHAR(32));

-- changeset liquibase:1748547963224-10 splitStatements:false
CREATE INDEX "idx_translation" ON "dictionary" USING btree("translation");

-- changeset liquibase:1748547963224-11 splitStatements:false
CREATE INDEX "channel_resource_idx" ON "learned_resources" USING btree("group_id", "resource_type");

-- changeset liquibase:1748547963224-12 splitStatements:false
ALTER TABLE "ch_question_replies" ADD CONSTRAINT "unique_channel_user_question_idx" UNIQUE ("channel_id", "user_id", "question_id");

-- changeset liquibase:1748547963224-13 splitStatements:false
CREATE INDEX "last_executed_at_idx" ON "tasks" USING btree("last_executed_at");

-- changeset liquibase:1748547963224-14 splitStatements:false
CREATE INDEX "tasks_channel_id_idx" ON "tasks" USING btree("channel_id");

-- changeset liquibase:1748547963224-15 splitStatements:false
ALTER TABLE "tasks" ADD CONSTRAINT "unique_channel_task_idx" UNIQUE ("channel_id", "task_name");

-- changeset liquibase:1748547963224-16 splitStatements:false
ALTER TABLE "application_version" ADD CONSTRAINT "application_version_version_key" UNIQUE ("version");

