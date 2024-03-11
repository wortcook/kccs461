load("@rules_java//java:defs.bzl", "java_binary")

java_binary (
    name = "hw1",
    srcs = glob(["src/main/java/edu/umkc/cs461/hw1/**/*.java"]),
    main_class = "edu.umkc.cs461.hw1.Main",
    deps = [
        # "@maven//:com_google_guava_guava",
    ],
    data = [
        "data/coordinates.csv",
        "data/Adjacencies.txt",
    ]
)
