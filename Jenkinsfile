pipeline {
    agent any

    tools {
        // Usa los nombres que configuraste en "Global Tool Configuration"
        maven 'MAVEN_HOME'
        jdk 'JAVA_HOME'
    }

    stages {
        stage('Checkout') {
            steps {
                // Jenkins descargará el código solo
                echo 'Descargando código de GitHub...'
            }
        }

        stage('Build & Compile') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('UI Tests (Selenium)') {
            steps {
                sh 'google-chrome --version || echo "Chrome no encontrado en el PATH"'
                sh 'mvn test'
            }
        }
    }

    post {
        always {
            echo 'Generando reportes de prueba...'
            // Aquí podrías agregar pasos para publicar reportes de TestNG
        }
    }
}