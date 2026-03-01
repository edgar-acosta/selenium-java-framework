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
                script {
                    // Este comando "unset" limpia las librerías del Snap 
                    // para que Chrome use las del sistema operativo
                    sh '''
                        export LD_LIBRARY_PATH=
                        export PATH=$PATH:/usr/bin
                        google-chrome --version || echo "Chrome sigue protestando pero intentaremos seguir"
                        mvn test
                    '''
                }
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