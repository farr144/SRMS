# SRMS (Student Record Management System)

A simple Java console application to manage university student records.

## Features
- Add / remove students
- Load & save to `students_data.txt`
- Reports:
  - GPA report (sorted high → low)
  - Ranking report (with ties)
  - Statistics report (counts + averages + department distribution)
- Multithreading:
  - Auto-save (runs on a scheduled background thread)
  - Background report generation to `reports/`

## How to Run (NetBeans / Ant)
Open the project in NetBeans and run **Main.java** (`srms1.Main`).

## How to Run (Command line)
From the `SRMS/` folder:
```bash
javac -d out -sourcepath src src/srms1/Main.java
java -cp out srms1.Main
```

## GitHub (Create & Push)
1) Create a new repository on GitHub (e.g. `SRMS`)
2) In the project folder:
```bash
git init
git add .
git commit -m "Initial SRMS project"
git branch -M main
git remote add origin <YOUR_REPO_URL>
git push -u origin main
```

## Suggested Team Workflow
- Create a branch per feature:
```bash
git checkout -b feature/reports
# work...
git add .
git commit -m "Add reports"
git push -u origin feature/reports
```
- Open a Pull Request to `main`
- Use Issues to track tasks (Reports / Auto-save / File I-O, etc.)
