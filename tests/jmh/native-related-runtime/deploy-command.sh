
# to deploy a jar file found in an external repo.
# only needed to run it once, but I'm keeping it around
# just in case i need to reference it again

mvn deploy:deploy-file \
    -Dfile=./jRAPL-repo/target/jRAPL-1.0.jar \
    -DgroupId=jRAPL \
    -DartifactId=jRAPL \
    -Dversion=1.0 \
    -Dpackaging=jar \
    -Durl=file:./jRAPL-repo/ \
    -DrepositoryId=jRAPL-repo \
    -DupdateReleaseInfo=true
