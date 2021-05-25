from flask import request, jsonify

from models import Technologie
from app import app


@app.route('/getAmountOfVulnerableWebsites', methods=['GET'])
def dispalyNumberOfAll():
    technologies = Technologie.query.all()
    numberOfTechnologies = len(technologies)
    amount = {}
    amount['amount'] = numberOfTechnologies
    return jsonify(amount)

@app.route('/getAllVulnerableWebsites', methods=['GET'])
def dispalyAll():
    technologies = Technologie.query.all()
    output = []
    for tech in technologies:
        currTech = {}
        currTech['id'] = tech.id
        currTech['name'] = tech.name
        currTech['category'] = tech.category
        currTech['app'] = tech.app
        currTech['version'] = tech.version
        output.append(currTech)
    return jsonify(output)

@app.route('/test', methods=['GET'])
def test():
    return {
        'test': 'test1'
    }