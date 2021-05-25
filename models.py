from app import db

class Technologie(db.Model):

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String)
    category = db.Column(db.String)
    app = db.Column(db.String)
    version = db.Column(db.String)

    def __init__(self, name, category, app, version):
        self.name = name
        self.category = category
        self.app = app
        self.version = version


if __name__ == "__main__":
    # Run this file directly to create the database tables.
    print("Creating database tables...")
    db.create_all()
    print("Done!")
