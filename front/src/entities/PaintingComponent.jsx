import React, { useState } from "react";
import BackendService from "../services/BackendService";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faSave } from "@fortawesome/free-solid-svg-icons";

import { Form } from "react-bootstrap";
import { useParams, useNavigate } from 'react-router-dom';

const PaintingComponent = props => {

    const [hidden, setHidden] = useState(false);
    const navigate = useNavigate();
    const [name, setName] = useState("")
    const [museumName, setMuseum] = useState()
    const [artistName, setArtist] = useState()
    const [id, setId] = useState(useParams().id)
    const [year, setYear] = useState()
    const updateName = (event) => {
        setName(event.target.value)
    }
    const updateMuseum = (event)=>{
        setMuseum(event.target.value)
    }
    const updateArtist = (event)=>{
        setArtist(event.target.value)
    }
    const updateYear = (event) => {
        setYear(event.target.value)
    }

    const onSubmit = (event) => {
        event.preventDefault();
        event.stopPropagation();
        let err = null;
        if (name === "") {
            err = "Имя должно быть указано"
        }
        let painting = {id: id, name: name, artist: {"id": id ,"name": artistName}, museum: {"name": museumName, "location": "Hj", "id": id}, year: year}
        if (parseInt(id) == -1) {
            BackendService.createPainting(painting)
                .catch(() => { })
                .finally(()=> {navigateToPaintings()})
        }
        else {
            let painting = {id: id, name: name, artist: {"id": id ,"name": artistName}, museum: {"name": museumName, "location": "hj", "id": id}, year: year}
            BackendService.updatePainting(painting)
                .catch(() => { })
                .finally(()=> {navigateToPaintings()})
        }
    }

    const navigateToPaintings = () => {
        navigate('/paintings')
    }

    if (hidden)
        return null;
    return (
        <div className="m-4">
            <div className="row my-2 mr-0">
                <h3>Данные картины</h3>
                <button
                    className="btn btn-outline-secondary ml-auto"
                    onClick={() => navigateToPaintings()}><FontAwesomeIcon
                    icon={faChevronLeft} />{' '}Назад</button>
            </div>
            <Form className="form-list" onSubmit={onSubmit}>
                <Form.Group>


                    <Form.Control
                        type="text"
                        placeholder="Введите название картины"
                        onChange={updateName}
                        value={name}
                        name="name"
                        autoComplete="off" />
                    <Form.Control
                        type="text"
                        placeholder="Введите имя художника"
                        onChange={updateArtist}
                        value={artistName}
                        name="name"
                        autoComplete="off" />
                    <Form.Control
                        type="text"
                        placeholder="Введите название музея"
                        onChange={updateMuseum}
                        value={museumName}
                        name="name"
                        autoComplete="off" />
                    <Form.Control
                        type="text"
                        placeholder="Введите год создания"
                        onChange={updateYear}
                        value={year}
                        name="name"
                        autoComplete="off" />

                </Form.Group>
                <button
                    className="btn btn-outline-secondary"
                    type="submit"><FontAwesomeIcon
                    icon={faSave} />{' '}Сохранить</button>
            </Form>
        </div>
    )

}

export default PaintingComponent;