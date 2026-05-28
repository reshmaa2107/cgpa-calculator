CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE student (
                         id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         name         VARCHAR(255) NOT NULL,
                         email        VARCHAR(255) NOT NULL UNIQUE,
                         roll_number  VARCHAR(100) NOT NULL UNIQUE,
                         created_at   TIMESTAMP DEFAULT NOW()
);

CREATE TABLE semester (
                          id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          student_id       UUID NOT NULL REFERENCES student(id) ON DELETE CASCADE,
                          semester_number  INTEGER NOT NULL,
                          academic_year    VARCHAR(20),
                          gpa              REAL,
                          UNIQUE (student_id, semester_number)
);

CREATE TABLE course (
                        id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        semester_id  UUID NOT NULL REFERENCES semester(id) ON DELETE CASCADE,
                        course_name  VARCHAR(255) NOT NULL,
                        course_code  VARCHAR(50),
                        credit_hours INTEGER NOT NULL CHECK (credit_hours > 0)
);

CREATE TABLE grade (
                       id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       course_id     UUID NOT NULL UNIQUE REFERENCES course(id) ON DELETE CASCADE,
                       letter_grade  VARCHAR(5) NOT NULL,
                       grade_points  REAL NOT NULL CHECK (grade_points >= 0)
);

CREATE INDEX idx_semester_student ON semester(student_id);
CREATE INDEX idx_course_semester  ON course(semester_id);
CREATE INDEX idx_grade_course     ON grade(course_id);